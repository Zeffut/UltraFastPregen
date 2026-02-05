package com.ultrafastpregen.generation;

import com.ultrafastpregen.UltraFastPregen;
import org.bukkit.World;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class GenerationManager {
    
    private final UltraFastPregen plugin;
    private final Map<World, GenerationSession> sessions;
    
    public GenerationManager(UltraFastPregen plugin) {
        this.plugin = plugin;
        this.sessions = new ConcurrentHashMap<>();
    }
    
    public synchronized GenerationSession startGeneration(World world, int radius, GenerationShape shape) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }
        
        // Arrêter la session existante pour ce monde si elle existe
        GenerationSession existingSession = sessions.get(world);
        if (existingSession != null && existingSession.isRunning()) {
            existingSession.stop();
        }
        
        GenerationSession session = new GenerationSession(plugin, world, radius, shape);
        sessions.put(world, session);
        session.start();
        return session;
    }
    
    public synchronized void stopGeneration(World world) {
        if (world == null) {
            return;
        }
        
        GenerationSession session = sessions.get(world);
        if (session != null) {
            session.stop();
            sessions.remove(world);
        }
    }
    
    public GenerationSession getActiveSession() {
        // Retourner n'importe quelle session active (priorité à la plus récente)
        for (GenerationSession session : sessions.values()) {
            if (session.isRunning()) {
                return session;
            }
        }
        return null;
    }
    
    public GenerationSession getSession(World world) {
        return sessions.get(world);
    }
    
    public synchronized boolean hasActiveSession(World world) {
        if (world == null) {
            return false;
        }
        
        GenerationSession session = sessions.get(world);
        return session != null && session.isRunning();
    }
    
    public synchronized void shutdown() {
        for (GenerationSession session : sessions.values()) {
            if (session.isRunning()) {
                session.stop();
            }
        }
        sessions.clear();
    }
    
    /**
     * Nettoie les sessions terminées
     */
    public synchronized void cleanupFinishedSessions() {
        sessions.entrySet().removeIf(entry -> !entry.getValue().isRunning());
    }
}
