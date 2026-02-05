package com.ultrafastpregen.generation;

import com.ultrafastpregen.UltraFastPregen;
import com.ultrafastpregen.utils.MessageUtil;
import com.ultrafastpregen.utils.RegionCache;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GenerationSession implements Runnable {
    
    private final UltraFastPregen plugin;
    private final World world;
    private final int radius;
    private final GenerationShape shape;
    private final int centerX;
    private final int centerZ;
    
    private final ConcurrentLinkedQueue<ChunkCoord> chunkQueue;
    private final AtomicInteger chunksGenerated;
    private final AtomicInteger chunksSkipped;  // Chunks déjà générés
    private final AtomicInteger totalChunks;
    private final AtomicLong startTime;
    private final RegionCache regionCache;  // Cache des régions
    
    private volatile boolean running;
    private volatile boolean paused;
    private volatile boolean stopped;
    
    // Statistiques en temps réel
    private final AtomicLong lastUpdate;
    private final AtomicInteger lastChunkCount;
    private volatile double currentSpeed;
    
    // Configuration ULTRA AGRESSIVE pour 2x vitesse
    private static final int MAX_WORKING_COUNT = getOptimalWorkingCount();
    private static final int BATCH_SIZE = 128; // Augmenté de 32 à 128 pour soumettre plus vite
    private static final int UPDATE_INTERVAL_MS = 1000;
    
    private static int getOptimalWorkingCount() {
        // BEAUCOUP plus agressif - on vise 2x Chunky
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        
        if (maxMemory >= 8192) {  // 8GB+
            return 500;  // 10x Chunky!
        } else if (maxMemory >= 4096) {  // 4GB+
            return 300;  // 6x Chunky
        } else if (maxMemory >= 2048) {  // 2GB+
            return 200;  // 4x Chunky
        } else {
            return 100;  // 2x Chunky minimum
        }
    }
    
    public GenerationSession(UltraFastPregen plugin, World world, int radius, GenerationShape shape) {
        this.plugin = plugin;
        this.world = world;
        this.radius = radius;
        this.shape = shape;
        this.centerX = world.getSpawnLocation().getBlockX() >> 4;
        this.centerZ = world.getSpawnLocation().getBlockZ() >> 4;
        
        this.chunkQueue = new ConcurrentLinkedQueue<>();
        this.chunksGenerated = new AtomicInteger(0);
        this.chunksSkipped = new AtomicInteger(0);
        this.totalChunks = new AtomicInteger(0);
        this.startTime = new AtomicLong(0);
        this.regionCache = new RegionCache(world);
        
        this.running = false;
        this.paused = false;
        this.stopped = false;
        this.lastUpdate = new AtomicLong(0);
        this.lastChunkCount = new AtomicInteger(0);
        this.currentSpeed = 0;
        
        generateChunkQueue();
    }
    
    private void generateChunkQueue() {
        int chunkRadius = (radius >> 4) + 1;
        
        // NOUVEAU: Pattern par région (32x32 chunks)
        // Plus efficace car Minecraft sauvegarde par région
        // On génère région par région pour optimiser les I/O
        
        int regionRadius = (chunkRadius >> 5) + 1; // Rayon en régions
        
        // Parcourir les régions en spirale
        int x = 0, z = 0;
        int dx = 0, dz = -1;
        int maxI = (regionRadius * 2 + 1) * (regionRadius * 2 + 1);
        
        for (int i = 0; i < maxI; i++) {
            if ((-regionRadius <= x && x <= regionRadius) && (-regionRadius <= z && z <= regionRadius)) {
                int regionX = (centerX >> 5) + x;
                int regionZ = (centerZ >> 5) + z;
                
                // Pour chaque région, ajouter tous ses chunks en pattern optimisé
                addChunksInRegion(regionX, regionZ, chunkRadius);
            }
            
            if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1 - z)) {
                int temp = dx;
                dx = -dz;
                dz = temp;
            }
            
            x += dx;
            z += dz;
        }
    }
    
    private void addChunksInRegion(int regionX, int regionZ, int chunkRadius) {
        // Une région = 32x32 chunks
        int baseChunkX = regionX << 5;
        int baseChunkZ = regionZ << 5;
        
        // Parcourir les chunks de la région en pattern de bande (meilleur pour cache)
        for (int z = 0; z < 32; z++) {
            for (int x = 0; x < 32; x++) {
                int chunkX = baseChunkX + x;
                int chunkZ = baseChunkZ + z;
                
                // Convertir en coordonnées de blocs pour la vérification
                int blockX = chunkX << 4;
                int blockZ = chunkZ << 4;
                int centerBlockX = centerX << 4;
                int centerBlockZ = centerZ << 4;
                
                // Vérifier si le chunk est dans la forme demandée
                boolean shouldGenerate;
                if (shape == GenerationShape.SQUARE) {
                    // Forme carrée: vérifier si dans les limites du carré
                    int deltaX = Math.abs(blockX - centerBlockX);
                    int deltaZ = Math.abs(blockZ - centerBlockZ);
                    shouldGenerate = deltaX <= radius && deltaZ <= radius;
                } else {
                    // Forme circulaire: vérifier la distance euclidienne
                    double distance = Math.sqrt(Math.pow(blockX - centerBlockX, 2) + Math.pow(blockZ - centerBlockZ, 2));
                    shouldGenerate = distance <= radius;
                }
                
                if (shouldGenerate) {
                    chunkQueue.add(new ChunkCoord(chunkX, chunkZ));
                    totalChunks.incrementAndGet();
                }
            }
        }
    }
    
    public void start() {
        // Lancer dans un thread séparé pour ne PAS bloquer le serveur
        Thread generationThread = new Thread(this, "UltraFastPregen-" + world.getName());
        generationThread.setPriority(Thread.NORM_PRIORITY + 1); // Légère priorité haute
        generationThread.start();
    }
    
    @Override
    public void run() {
        running = true;
        startTime.set(System.currentTimeMillis());
        lastUpdate.set(System.currentTimeMillis());
        
        // OPTIMISATION: Désactiver features inutiles pour accélérer
        final boolean originalKeepSpawnInMemory = world.getKeepSpawnInMemory();
        
        // Désactiver le spawn loading (économise CPU) - DOIT être synchrone
        Bukkit.getScheduler().runTask(plugin, () -> {
            world.setKeepSpawnInMemory(false);
        });
        
        try {
            performGeneration();
        } finally {
            // Restaurer les paramètres originaux - DOIT être synchrone
            Bukkit.getScheduler().runTask(plugin, () -> {
                world.setKeepSpawnInMemory(originalKeepSpawnInMemory);
            });
        }
        
        if (!stopped) {
            complete();
        }
    }
    
    private void performGeneration() {
        // Semaphore pour limiter le nombre de chunks en cours de génération
        final Semaphore working = new Semaphore(MAX_WORKING_COUNT);
        
        plugin.getLogger().info("Démarrage de la génération: " + totalChunks.get() + " chunks");
        plugin.getLogger().info("Configuration: MAX_WORKING_COUNT=" + MAX_WORKING_COUNT + ", BATCH_SIZE=" + BATCH_SIZE);
        plugin.getLogger().info("RegionCache: " + regionCache.getRegionCount() + " régions existantes détectées");
        
        // Traiter tous les chunks de la queue
        while (!stopped && !chunkQueue.isEmpty()) {
            if (paused) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    cleanup();
                    return;
                }
                continue;
            }
            
            // Soumettre un lot de chunks
            int batchCount = 0;
            while (batchCount < BATCH_SIZE && !chunkQueue.isEmpty()) {
                ChunkCoord coord = chunkQueue.poll();
                if (coord == null) break;
                
                // OPTIMISATION: Utiliser le RegionCache pour skip rapidement
                if (regionCache.isChunkGenerated(coord.x, coord.z)) {
                    // Chunk déjà généré, on skip
                    chunksSkipped.incrementAndGet();
                    chunksGenerated.incrementAndGet();
                    batchCount++;
                    continue;
                }
                
                try {
                    // Acquérir un slot dans le semaphore avec timeout
                    if (!working.tryAcquire(30, java.util.concurrent.TimeUnit.SECONDS)) {
                        plugin.getLogger().warning("Timeout lors de l'acquisition du semaphore");
                        continue;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    cleanup();
                    return;
                }
                
                final ChunkCoord finalCoord = coord;
                
                // Générer le chunk de manière asynchrone
                world.getChunkAtAsync(coord.x, coord.z, true)
                    .whenComplete((chunk, throwable) -> {
                        // Libérer le slot dans le semaphore
                        working.release();
                        
                        if (throwable == null && chunk != null) {
                            chunksGenerated.incrementAndGet();
                            regionCache.markChunkGenerated(finalCoord.x, finalCoord.z);
                            updateStatistics();
                        } else {
                            plugin.getLogger().warning("Erreur génération chunk (" + 
                                finalCoord.x + ", " + finalCoord.z + "): " + 
                                (throwable != null ? throwable.getMessage() : "chunk null"));
                        }
                    });
                
                batchCount++;
            }
        }
        
        // Attendre que tous les chunks en cours soient terminés (avec timeout)
        try {
            if (!working.tryAcquire(MAX_WORKING_COUNT, 60, java.util.concurrent.TimeUnit.SECONDS)) {
                plugin.getLogger().warning("Timeout lors de l'attente de fin de génération");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            cleanup();
        }
    }
    
    /**
     * Nettoie les ressources après la génération
     */
    private void cleanup() {
        // Vider la queue pour libérer la mémoire
        chunkQueue.clear();
        
        plugin.getLogger().info("Nettoyage terminé - Mémoire libérée");
    }
    
    private synchronized void updateStatistics() {
        long now = System.currentTimeMillis();
        long lastUpdateTime = lastUpdate.get();
        
        if (now - lastUpdateTime >= UPDATE_INTERVAL_MS) {
            int currentCount = chunksGenerated.get();
            int lastCount = lastChunkCount.get();
            
            // Calculer la vitesse (chunks/sec)
            long timeDiff = now - lastUpdateTime;
            if (timeDiff > 0) {
                int chunkDiff = currentCount - lastCount;
                currentSpeed = (chunkDiff * 1000.0) / timeDiff;
            }
            
            lastUpdate.set(now);
            lastChunkCount.set(currentCount);
        }
    }
    
    public void pause() {
        paused = true;
    }
    
    public void resume() {
        paused = false;
        lastUpdate.set(System.currentTimeMillis());
        lastChunkCount.set(chunksGenerated.get());
    }
    
    public void stop() {
        stopped = true;
        running = false;
    }
    
    private void complete() {
        stop();
        
        long totalTime = System.currentTimeMillis() - startTime.get();
        double avgSpeed = (chunksGenerated.get() * 1000.0) / totalTime;
        
        // Annoncer sur le serveur de manière synchrone
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.broadcast(MessageUtil.format(""), "ultrafastpregen.use");
            Bukkit.broadcast(MessageUtil.format("&a&l⚡ GÉNÉRATION TERMINÉE! ⚡"), "ultrafastpregen.use");
            Bukkit.broadcast(MessageUtil.format(""), "ultrafastpregen.use");
            Bukkit.broadcast(MessageUtil.format("&fMonde: &b" + world.getName()), "ultrafastpregen.use");
            Bukkit.broadcast(MessageUtil.format("&fForme: &b" + shape.getDisplayName()), "ultrafastpregen.use");
            Bukkit.broadcast(MessageUtil.format("&fChunks générés: &b" + chunksGenerated.get()), "ultrafastpregen.use");
            Bukkit.broadcast(MessageUtil.format("&fTemps total: &b" + formatTime(totalTime)), "ultrafastpregen.use");
            Bukkit.broadcast(MessageUtil.format("&fVitesse moyenne: &b" + String.format("%.1f", avgSpeed) + " chunks/sec"), "ultrafastpregen.use");
            Bukkit.broadcast(MessageUtil.format(""), "ultrafastpregen.use");
        });
    }
    
    public void sendStatus(CommandSender sender) {
        if (!running) {
            MessageUtil.send(sender, "&cAucune génération en cours");
            return;
        }
        
        int generated = chunksGenerated.get();
        int total = totalChunks.get();
        double percentage = (generated * 100.0) / total;
        
        long elapsed = System.currentTimeMillis() - startTime.get();
        long eta = currentSpeed > 0 ? (long) ((total - generated) / currentSpeed * 1000) : 0;
        
        double tps = getTPS();
        
        MessageUtil.send(sender, "");
        MessageUtil.send(sender, "&a&l⚡ ULTRA FAST PREGEN &7- Statut");
        MessageUtil.send(sender, "");
        MessageUtil.send(sender, "&fMonde: &b" + world.getName());
        MessageUtil.send(sender, "&fForme: &b" + shape.getDisplayName());
        MessageUtil.send(sender, "&fProgression: &b" + generated + "&7/&b" + total + " &7(" + String.format("%.1f", percentage) + "%)");
        MessageUtil.send(sender, createProgressBar(percentage));
        MessageUtil.send(sender, "&fVitesse: &b" + String.format("%.1f", currentSpeed) + " chunks/sec");
        MessageUtil.send(sender, "&fTemps écoulé: &b" + formatTime(elapsed));
        MessageUtil.send(sender, "&fTemps restant: &b" + (eta > 0 ? formatTime(eta) : "Calcul..."));
        MessageUtil.send(sender, "&fTPS: &b" + String.format("%.1f", tps));
        MessageUtil.send(sender, "&fMax concurrent chunks: &b" + MAX_WORKING_COUNT);
        MessageUtil.send(sender, "");
    }
    
    private double getTPS() {
        try {
            return Bukkit.getTPS()[0];
        } catch (Exception e) {
            return 20.0;
        }
    }
    
    private String createProgressBar(double percentage) {
        int barLength = 30;
        int filled = (int) (barLength * percentage / 100.0);
        
        StringBuilder bar = new StringBuilder("&7[");
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                bar.append("&a▰");
            } else {
                bar.append("&7▱");
            }
        }
        bar.append("&7]");
        
        return MessageUtil.format(bar.toString());
    }
    
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }
    
    // Getters
    public World getWorld() {
        return world;
    }
    
    public int getTotalChunks() {
        return totalChunks.get();
    }
    
    public boolean isRunning() {
        return running;
    }
    
    // Classe interne pour les coordonnées de chunks
    private static class ChunkCoord {
        final int x;
        final int z;
        
        ChunkCoord(int x, int z) {
            this.x = x;
            this.z = z;
        }
    }
}
