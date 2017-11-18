package org.dragonet.bukkit.lnations;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

/**
 * Created on 2017/11/13.
 */
public final class Lang {

    public static YamlConfiguration lang;

    public static String build(String path, Object... args) {
        return String.format(ChatColor.translateAlternateColorCodes('&', lang.getString(path)), args);
    }

    public static List<String> getStringList(String path) {
        return lang.getStringList(path);
    }

    public static void sendMessage(CommandSender sender, String path, Object... args) {
        sender.sendMessage(build("prefix") + build(path, args));
    }

    public static void sendMessageList(CommandSender sender, String path) {
        sender.sendMessage(build("prefix-list", build("prefix")));
        getStringList(path).forEach((s) -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
    }

}
