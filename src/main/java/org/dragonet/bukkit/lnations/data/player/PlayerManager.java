package org.dragonet.bukkit.lnations.data.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.dragonet.bukkit.lnations.EmptyValues;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.data.nation.Nation;
import org.dragonet.bukkit.lnations.data.nation.NationPermission;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created on 2017/11/17.
 */
public class PlayerManager implements Listener {

    private final LegendaryNationsPlugin plugin;

    private final File profilesFolder;
    private final File uuidMapFolder;

    public PlayerManager(LegendaryNationsPlugin plugin) {
        this.plugin = plugin;
        profilesFolder = new File(plugin.getDataFolder(), "profiles");
        uuidMapFolder = new File(plugin.getDataFolder(), "uuid");
        profilesFolder.mkdirs();
        uuidMapFolder.mkdirs();
    }

    public List<String> getNationNames(OfflinePlayer player) {
        File f = getProfileFile(player);
        if(!f.exists()) {
            return Collections.emptyList();
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(f);
        return configuration.getStringList("nations");
    }

    public List<Nation> getNations(OfflinePlayer player) {
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

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent e) {
        File uuidFile = new File(uuidMapFolder, e.getPlayer().getName().toLowerCase() + ".yml");
        if(uuidFile.exists()) return;
        YamlConfiguration conf = new YamlConfiguration();
        conf.set("uuid", e.getPlayer().getUniqueId().toString());
        try {
            conf.save(uuidFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * get nations for a player who has specific permissions
     * @param player
     * @param permission
     * @return
     */
    public List<Nation> getNationsWithPermission(OfflinePlayer player, NationPermission permission) {
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
    public boolean addToNation(OfflinePlayer player, String name) {
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
     * @param name
     * @return true when changes made, false when no need to change anything
     */
    public boolean removeFromNation(OfflinePlayer player, String name) {
        File f = getProfileFile(player);
        YamlConfiguration configuration;
        if(!f.exists()) {
            return false;
        } else {
            configuration = YamlConfiguration.loadConfiguration(f);
            if(!configuration.getStringList("nations").contains(name.toLowerCase())) return false;
        }
        List<String> nations = new ArrayList<>();// configuration.getStringList("nations");
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
            // player.sendMessage("\u00a7cERROR");
            return false;
        }
        plugin.getNationManager().getNation(name).removeMember(player);
        return true;
    }

    public boolean isInNation(OfflinePlayer player, String name) {
        File f = getProfileFile(player);
        if(!f.exists()) return false;
        return YamlConfiguration.loadConfiguration(f).getStringList("nations").contains(name.toLowerCase());
    }

    public UUID tryGetUUID(String name) {
        String filtered_name = new String(name.replace(File.separator, "").replace("..", "")).toLowerCase();
        File f = new File(uuidMapFolder, filtered_name + ".yml");
        if(!f.exists()) return null;
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
        return UUID.fromString(conf.getString("uuid"));
    }

    public File getProfileFile(UUID player) {
        return new File(profilesFolder, player + ".yml");
    }

    public File getProfileFile(OfflinePlayer player) {
        return new File(profilesFolder, player.getUniqueId() + ".yml");
    }

    public static YamlConfiguration initializePlayerProfile(OfflinePlayer player) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("nations", EmptyValues.MAP);
        return configuration;
    }
}
