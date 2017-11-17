package org.dragonet.bukkit.lnations;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

/**
 * Created on 2017/11/13.
 */
public final class Lang {

    public static YamlConfiguration lang;

    public static String build(String path, Object... args) {
        return String.format(lang.getString(path), args);
    }

    public static List<String> getStringList(String path) {
        return lang.getStringList(path);
    }

    public static void sendMessage(CommandSender sender, String path, Object... args) {
        sender.sendMessage(build(path, args));
    }

    public static void sendMessageList(CommandSender sender, String path) {
        getStringList(path).forEach(sender::sendMessage);
    }

}
