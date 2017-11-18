package org.dragonet.bukkit.lnations.data.player;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.dragonet.bukkit.lnations.Lang;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.data.nation.Nation;
import org.dragonet.bukkit.lnations.data.nation.NationPermission;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created on 2017/11/17.
 */
public class PlayerManager {

    private final LegendaryNationsPlugin plugin;

    private final File profilesFolder;

    public PlayerManager(LegendaryNationsPlugin plugin) {
        this.plugin = plugin;
        profilesFolder = new File(plugin.getDataFolder(), "profiles");
        profilesFolder.mkdirs();
    }

    public List<String> getNationNames(Player player) {
        File f = getProfileFile(player);
        if(!f.exists()) {
            return Collections.emptyList();
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(f);
        return configuration.getStringList("nations");
    }

    public List<Nation> getNations(Player player) {
        List<String> nation_names = getNationNames(player);
        List<Nation> nations = new ArrayList<>(nation_names.size());
        for(String name : nation_names) {
            Nation n = LegendaryNationsPlugin.getInstance().getNationManager().getNation(name);
            if(n == null) {
                LegendaryNationsPlugin.getInstance().getPlayerManager().removeFromNation(player, name);
                continue;
            }
            // message += Lang.build(n.getLeader().equals(player.getUniqueId()) ? "my.as-leader" : "my.as-member", n.getName());
            nations.add(n);
        }
        return Collections.unmodifiableList(nations);
    }

    /**
     * get nations for a player who has specific permissions
     * @param player
     * @param permission
     * @return
     */
    public List<Nation> getNationsWithPermission(Player player, NationPermission permission) {
        // we copy the code because we want better performance
        List<String> nation_names = getNationNames(player);
        List<Nation> nations = new ArrayList<>(nation_names.size());
        for(String name : nation_names) {
            Nation n = LegendaryNationsPlugin.getInstance().getNationManager().getNation(name);
            if(n == null) {
                LegendaryNationsPlugin.getInstance().getPlayerManager().removeFromNation(player, name);
                continue;
            }
            // message += Lang.build(n.getLeader().equals(player.getUniqueId()) ? "my.as-leader" : "my.as-member", n.getName());
            if(player.getUniqueId().equals(n.getLeader()) || n.hasPermission(player, permission)) {
                nations.add(n);
            }
        }
        return Collections.unmodifiableList(nations);
    }

    /**
     * adds a player to a nation
     * @param player
     * @param name
     * @return true when changes made, false when no need to change anything
     */
    public boolean addToNation(Player player, String name) {
        File f = getProfileFile(player);
        YamlConfiguration configuration;
        if(!f.exists()) {
            configuration = initializePlayerProfile(player);
        } else {
            configuration = YamlConfiguration.loadConfiguration(f);
            if(configuration.getStringList("nations").contains(name)) return false;
        }
        List<String> nations = new ArrayList<>();
        nations.addAll(configuration.getStringList("nations"));
        nations.add(name.toLowerCase());
        configuration.set("nations", nations);
        try {
            configuration.save(f);
        } catch (IOException e) {
            e.printStackTrace();
            // player.sendMessage("\u00a7ERROR");
            return false;
        }
        plugin.getNationManager().getNation(name).addMember(player);
        return true;
    }

    /**
     * removes a player
     * @param player
     * @param name true when changes made, false when no need to change anything
     * @return
     */
    public boolean removeFromNation(Player player, String name) {
        File f = getProfileFile(player);
        YamlConfiguration configuration;
        if(!f.exists()) {
            return false;
        } else {
            configuration = YamlConfiguration.loadConfiguration(f);
            if(configuration.getStringList("nations").contains(name)) return false;
        }
        List<String> nations = new ArrayList();// configuration.getStringList("nations");
        nations.addAll(configuration.getStringList("nations"));
        nations.remove(name.toLowerCase());
        if(nations.size() <= 0) {
            // no nations? shame
            f.delete();
            return true;
        }
        configuration.set("nations", nations);
        try {
            configuration.save(f);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage("\u00a7cERROR");
            return false;
        }
        plugin.getNationManager().getNation(name).removeMember(player);
        return true;
    }

    public boolean isInNation(Player player, String name) {
        File f = getProfileFile(player);
        if(!f.exists()) return false;
        return YamlConfiguration.loadConfiguration(f).getStringList("nations").contains(name.toLowerCase());
    }

    public File getProfileFile(UUID player) {
        return new File(profilesFolder, player + ".yml");
    }

    public File getProfileFile(Player player) {
        return new File(profilesFolder, player.getUniqueId() + ".yml");
    }

    public static YamlConfiguration initializePlayerProfile(Player player) {
        YamlConfiguration configuration = new YamlConfiguration();
        List<String> str = Collections.emptyList();
        configuration.set("nations", str);
        return configuration;
    }
}
