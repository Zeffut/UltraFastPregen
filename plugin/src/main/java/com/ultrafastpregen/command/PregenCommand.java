package com.ultrafastpregen.command;

import com.ultrafastpregen.UltraFastPregen;
import com.ultrafastpregen.generation.GenerationManager;
import com.ultrafastpregen.generation.GenerationSession;
import com.ultrafastpregen.utils.MessageUtil;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PregenCommand implements CommandExecutor, TabCompleter {
    
    private final UltraFastPregen plugin;
    private final GenerationManager generationManager;
    
    public PregenCommand(UltraFastPregen plugin) {
        this.plugin = plugin;
        this.generationManager = plugin.getGenerationManager();
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "start":
                return handleStart(sender, args);
            case "stop":
                return handleStop(sender);
            case "pause":
                return handlePause(sender);
            case "resume":
                return handleResume(sender);
            case "status":
                return handleStatus(sender);
            case "help":
                sendHelp(sender);
                return true;
            default:
                MessageUtil.send(sender, "&cCommande inconnue. Utilisez /pregen help");
                return true;
        }
    }
    
    private boolean handleStart(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.send(sender, "&cUsage: /pregen start <rayon> [monde] [forme]");
            MessageUtil.send(sender, "&7Formes disponibles: square (défaut), circle");
            return true;
        }
        
        int radius;
        try {
            radius = Integer.parseInt(args[1]);
            if (radius <= 0) {
                MessageUtil.send(sender, "&cLe rayon doit être positif");
                return true;
            }
            if (radius > 50000) {
                MessageUtil.send(sender, "&cLe rayon maximum est de 50000 blocs");
                MessageUtil.send(sender, "&7Pour éviter une surcharge du serveur");
                return true;
            }
        } catch (NumberFormatException e) {
            MessageUtil.send(sender, "&cRayon invalide: " + args[1]);
            MessageUtil.send(sender, "&7Le rayon doit être un nombre entier");
            return true;
        }
        
        World world = null;
        com.ultrafastpregen.generation.GenerationShape shape = com.ultrafastpregen.generation.GenerationShape.SQUARE;
        
        if (args.length >= 3) {
            World testWorld = plugin.getServer().getWorld(args[2]);
            if (testWorld != null) {
                world = testWorld;
            } else {
                // Si args[2] n'est pas un monde, peut-être que c'est la forme
                shape = com.ultrafastpregen.generation.GenerationShape.fromString(args[2]);
            }
        }
        
        // Si on n'a pas trouvé de monde, utiliser le monde du joueur ou le premier monde
        if (world == null) {
            if (sender instanceof Player) {
                world = ((Player) sender).getWorld();
            } else {
                if (plugin.getServer().getWorlds().isEmpty()) {
                    MessageUtil.send(sender, "&cAucun monde disponible!");
                    return true;
                }
                world = plugin.getServer().getWorlds().get(0);
            }
        }
        
        // Si on a 4 arguments: /pregen start <rayon> <monde> <forme>
        if (args.length >= 4) {
            shape = com.ultrafastpregen.generation.GenerationShape.fromString(args[3]);
        }
        
        // Vérification finale: le monde ne doit pas être null
        if (world == null) {
            MessageUtil.send(sender, "&cMonde invalide!");
            return true;
        }
        
        if (generationManager.hasActiveSession(world)) {
            MessageUtil.send(sender, "&cUne génération est déjà en cours pour ce monde!");
            return true;
        }
        
        GenerationSession session;
        try {
            session = generationManager.startGeneration(world, radius, shape);
        } catch (Exception e) {
            MessageUtil.send(sender, "&cErreur lors du démarrage: " + e.getMessage());
            plugin.getLogger().severe("Erreur lors du démarrage de la génération: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
        
        String shapeDisplay = shape == com.ultrafastpregen.generation.GenerationShape.SQUARE ? 
            (radius * 2) + "x" + (radius * 2) : "rayon " + radius;
        
        MessageUtil.send(sender, "");
        MessageUtil.send(sender, "&a&l⚡ ULTRA FAST PREGEN ⚡");
        MessageUtil.send(sender, "&7Démarrage de la pré-génération...");
        MessageUtil.send(sender, "");
        MessageUtil.send(sender, "&fMonde: &b" + world.getName());
        MessageUtil.send(sender, "&fForme: &b" + shape.getDisplayName());
        MessageUtil.send(sender, "&fRayon: &b" + radius + " &7blocs &8(" + shapeDisplay + ")");
        MessageUtil.send(sender, "&fChunks estimés: &b" + session.getTotalChunks());
        MessageUtil.send(sender, "");
        MessageUtil.send(sender, "&aGénération lancée! Utilisez &f/pregen status &apour suivre la progression");
        MessageUtil.send(sender, "");
        
        return true;
    }
    
    private boolean handleStop(CommandSender sender) {
        GenerationSession session = generationManager.getActiveSession();
        if (session == null) {
            MessageUtil.send(sender, "&cAucune génération en cours");
            return true;
        }
        
        World world = session.getWorld();
        generationManager.stopGeneration(world);
        MessageUtil.send(sender, "&eGénération arrêtée pour le monde: &b" + world.getName());
        return true;
    }
    
    private boolean handlePause(CommandSender sender) {
        GenerationSession session = generationManager.getActiveSession();
        if (session == null) {
            MessageUtil.send(sender, "&cAucune génération en cours");
            return true;
        }
        
        session.pause();
        MessageUtil.send(sender, "&eGénération mise en pause");
        return true;
    }
    
    private boolean handleResume(CommandSender sender) {
        GenerationSession session = generationManager.getActiveSession();
        if (session == null) {
            MessageUtil.send(sender, "&cAucune génération en cours");
            return true;
        }
        
        session.resume();
        MessageUtil.send(sender, "&aGénération reprise");
        return true;
    }
    
    private boolean handleStatus(CommandSender sender) {
        GenerationSession session = generationManager.getActiveSession();
        if (session == null) {
            MessageUtil.send(sender, "&cAucune génération en cours");
            return true;
        }
        
        session.sendStatus(sender);
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        MessageUtil.send(sender, "");
        MessageUtil.send(sender, "&a&l⚡ ULTRA FAST PREGEN &7- Aide");
        MessageUtil.send(sender, "");
        MessageUtil.send(sender, "&f/pregen start <rayon> [monde] [forme] &7- Démarrer la génération");
        MessageUtil.send(sender, "&7  Formes: square (défaut), circle");
        MessageUtil.send(sender, "&f/pregen stop &7- Arrêter la génération");
        MessageUtil.send(sender, "&f/pregen pause &7- Mettre en pause");
        MessageUtil.send(sender, "&f/pregen resume &7- Reprendre");
        MessageUtil.send(sender, "&f/pregen status &7- Voir la progression");
        MessageUtil.send(sender, "&f/pregen help &7- Afficher cette aide");
        MessageUtil.send(sender, "");
    }
    
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                 @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.addAll(Arrays.asList("start", "stop", "pause", "resume", "status", "help"));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("start")) {
            // Peut être un monde OU une forme
            for (World world : plugin.getServer().getWorlds()) {
                completions.add(world.getName());
            }
            completions.addAll(Arrays.asList("square", "circle"));
        } else if (args.length == 4 && args[0].equalsIgnoreCase("start")) {
            // Si 4e argument, c'est forcément la forme
            completions.addAll(Arrays.asList("square", "circle"));
        }
        
        return completions;
    }
}
