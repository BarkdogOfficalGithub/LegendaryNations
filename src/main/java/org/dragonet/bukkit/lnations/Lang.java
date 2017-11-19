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

    public static void sendMessage(Object sender, String path, Object... args) {
        if(!CommandSender.class.isAssignableFrom(sender.getClass())) return;
        ((CommandSender)sender).sendMessage(build("prefix") + " " + build(path, args));
    }

    public static void sendMessageList(Object sender, String path) {
        if(!CommandSender.class.isAssignableFrom(sender.getClass())) return;
        ((CommandSender)sender).sendMessage(build("prefix-list", build("prefix")));
        getStringList(path).forEach((s) -> ((CommandSender)sender).sendMessage(s));
    }

}
