package land.esta.macrocheck.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CC {
    public static final String prefix = "";

    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }
}