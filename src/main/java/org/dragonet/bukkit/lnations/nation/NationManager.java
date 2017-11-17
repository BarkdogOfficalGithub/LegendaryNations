package org.dragonet.bukkit.lnations.nation;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The main nation manager
 * Created on 2017/11/17.
 */
public final class NationManager implements Listener, Runnable {

    private final LegendaryNationsPlugin plugin;

    private File nationsFolder;

    /**
     * KEY IS IN LOWER CASE!!
      */
    private Map<String, Nation> nationCache = new HashMap<>();

    public NationManager(LegendaryNationsPlugin plugin) {
        this.plugin = plugin;
        nationsFolder = new File(plugin.getDataFolder(), "nations");
        nationsFolder.mkdirs();
    }

    public Nation getNation(String name) {
        if(nationCache.containsKey(name.toLowerCase())) return nationCache.get(name.toLowerCase());
        File nationFile = getNationFile(name);
        if(!nationFile.exists()) return null;
        Nation nation = new Nation(nationFile);
        nationCache.put(name.toLowerCase(), nation);
        return nation;
    }

    public boolean exists(String name) {
        if(nationCache.containsKey(name.toLowerCase())) return true;
        return getNationFile(name).exists();
    }

    /**
     * create a nation, it will NOT check player's balance
     * @param name
     * @return null if conflicts
     */
    public Nation createNation(String name, Player leader) {
        if(exists(name)) return null;
        YamlConfiguration internal = Nation.initializeNation(name, leader.getUniqueId());
        try {
            internal.save(getNationFile(name));
        } catch (IOException e) {
            e.printStackTrace();
            leader.sendMessage("\u00a7cERROR");
            return null;
        }
        return getNation(name);
    }

    /**
     * clean up task
     * 30min no access before remove
     */
    @Override
    public void run() {
        Iterator<Map.Entry<String, Nation>> iterator = nationCache.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, Nation> c = iterator.next();
            if(System.currentTimeMillis() - c.getValue().last_access_time > 1000L * 60L * 30L) {
                c.getValue().saveConfiguration();
                iterator.remove();
            }
        }
    }

    public File getNationFile(String name) {
        return new File(nationsFolder, name.toLowerCase() + ".yml");
    }
}
