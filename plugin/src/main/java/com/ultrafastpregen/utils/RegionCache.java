package com.ultrafastpregen.utils;

import org.bukkit.World;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache optimisé pour tracker rapidement quels chunks sont déjà générés
 * En lisant les fichiers de région (.mca) au lieu de demander à Bukkit
 * 
 * Minecraft sauvegarde les chunks par fichiers de région:
 * - 1 région = 32x32 chunks = 512x512 blocs
 * - Fichier: r.X.Z.mca (exemple: r.0.0.mca, r.-1.2.mca)
 * 
 * Thread-safe pour utilisation concurrente
 */
public class RegionCache {
    
    private final Set<String> existingRegions;
    private final File regionFolder;
    
    public RegionCache(World world) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }
        
        this.regionFolder = new File(world.getWorldFolder(), "region");
        this.existingRegions = ConcurrentHashMap.newKeySet();
        
        // Scanner rapidement tous les fichiers .mca existants
        loadExistingRegions();
    }
    
    private void loadExistingRegions() {
        if (!regionFolder.exists() || !regionFolder.isDirectory()) {
            return;
        }
        
        File[] regionFiles = regionFolder.listFiles((dir, name) -> name.endsWith(".mca"));
        
        if (regionFiles != null) {
            for (File file : regionFiles) {
                // Extraire les coordonnées du nom de fichier
                // Format: r.X.Z.mca
                String name = file.getName();
                if (name.length() > 4) {
                    String coords = name.substring(0, name.length() - 4); // Enlever ".mca"
                    existingRegions.add(coords);
                }
            }
        }
    }
    
    /**
     * Vérifie si un chunk est potentiellement généré
     * Plus rapide que world.isChunkGenerated() car ne lit que le nom de fichier
     * 
     * Thread-safe
     * 
     * @param chunkX Coordonnée X du chunk
     * @param chunkZ Coordonnée Z du chunk
     * @return true si la région contenant ce chunk existe (chunk probablement généré)
     */
    public boolean isChunkGenerated(int chunkX, int chunkZ) {
        // Convertir coordonnées chunk -> coordonnées région
        // 1 région = 32 chunks
        int regionX = chunkX >> 5;  // Division par 32
        int regionZ = chunkZ >> 5;
        
        String regionKey = "r." + regionX + "." + regionZ;
        
        return existingRegions.contains(regionKey);
    }
    
    /**
     * Marque un chunk comme généré (met à jour le cache)
     * Thread-safe
     */
    public void markChunkGenerated(int chunkX, int chunkZ) {
        int regionX = chunkX >> 5;
        int regionZ = chunkZ >> 5;
        
        String regionKey = "r." + regionX + "." + regionZ;
        existingRegions.add(regionKey);
    }
    
    /**
     * Retourne le nombre de régions existantes
     * Thread-safe
     */
    public int getRegionCount() {
        return existingRegions.size();
    }
}
