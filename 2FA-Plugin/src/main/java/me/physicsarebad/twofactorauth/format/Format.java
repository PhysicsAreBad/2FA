package me.physicsarebad.twofactorauth.format;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;

public class Format {
    /**
     * Retrieve and format message from config.yml
     * @param path The path to the message in the config.yml file
     * @param config The config file that has the prefix and message
     * @return Returns TextComponent to send to a player
     */
    public static TextComponent getColorMessage(String path, Configuration config) {
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', (String) config.get("messages.prefix")) + " " +ChatColor.GRAY+ ChatColor.translateAlternateColorCodes('&', (String) config.get(path)));
    }

    /**
     * Retrieve and format given message
     * @param string The message to format
     * @param config The config file that has the prefix
     * @return Returns TextComponent to send to a player
     */
    public static TextComponent getColorString(String string, Configuration config) {
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', (String) config.get("messages.prefix")) + " " + string);
    }
}
