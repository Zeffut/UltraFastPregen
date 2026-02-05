package com.ultrafastpregen.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtil {
    
    public static void send(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    
    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
