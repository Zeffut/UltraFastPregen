package com.ultrafastpregen;

import com.ultrafastpregen.command.PregenCommand;
import com.ultrafastpregen.generation.GenerationManager;
import org.bukkit.plugin.java.JavaPlugin;

public class UltraFastPregen extends JavaPlugin {
    
    private static UltraFastPregen instance;
    private GenerationManager generationManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        getLogger().info("========================================");
        getLogger().info("  ⚡ ULTRA FAST PREGEN ⚡");
        getLogger().info("  Version: " + getDescription().getVersion());
        getLogger().info("  Plugin de pré-génération ultra-rapide");
        getLogger().info("  Pour Paper 1.21.11");
        getLogger().info("========================================");
        
        // Initialiser le gestionnaire de génération
        generationManager = new GenerationManager(this);
        getLogger().info("✓ Generation Manager initialisé");
        
        // Enregistrer les commandes
        PregenCommand pregenCommand = new PregenCommand(this);
        org.bukkit.command.PluginCommand command = getCommand("pregen");
        if (command == null) {
            getLogger().severe("ERREUR: Commande 'pregen' non trouvée dans plugin.yml!");
            getLogger().severe("Le plugin ne pourra pas fonctionner correctement.");
            return;
        }
        command.setExecutor(pregenCommand);
        command.setTabCompleter(pregenCommand);
        getLogger().info("✓ Commandes enregistrées");
        
        getLogger().info("========================================");
        getLogger().info("  Plugin activé avec succès!");
        getLogger().info("  Utilisez /pregen help pour commencer");
        getLogger().info("========================================");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Arrêt des générations en cours...");
        if (generationManager != null) {
            generationManager.shutdown();
        }
        getLogger().info("UltraFastPregen désactivé!");
    }
    
    public static UltraFastPregen getInstance() {
        return instance;
    }
    
    public GenerationManager getGenerationManager() {
        return generationManager;
    }
}
