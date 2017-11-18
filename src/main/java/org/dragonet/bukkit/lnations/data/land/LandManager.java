package org.dragonet.bukkit.lnations.data.land;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * the main lnations manager, manage chunk configurations
 * Created on 2017/11/17.
 */
public class LandManager implements Listener {

    private final LegendaryNationsPlugin plugin;

    private final Map<UUID, WorldLandManager> worlds = new HashMap<>();
    private final File regionsFolder;

    public LandManager(LegendaryNationsPlugin plugin) {
        this.plugin = plugin;
        regionsFolder = new File(plugin.getDataFolder(), "regions");
    }

    public WorldLandManager getWorldManager(World world) {
        return worlds.get(world.getUID());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onWorldLoad(WorldLoadEvent e) {
        File file = new File(regionsFolder, e.getWorld().getName());
        file.mkdirs();
        worlds.put(e.getWorld().getUID(), new WorldLandManager(e.getWorld(), plugin, file));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onWorldUnload(WorldUnloadEvent e) {
        WorldLandManager w = worlds.remove(e.getWorld().getUID());
        if(w == null) return;
        w.cleanAndSave();
        worlds.remove(e.getWorld().getUID());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onChunkLoad(ChunkLoadEvent e) {
        WorldLandManager w = worlds.get(e.getWorld().getUID());
        if(w == null) return;
        w.onChunkLoad(e.getChunk());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onChunkUnload(ChunkUnloadEvent e) {
        WorldLandManager w = worlds.get(e.getWorld().getUID());
        if(w == null) return;
        w.onChunkUnload(e.getChunk());
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        if(worlds.containsKey(e.getPlayer().getWorld().getUID())) return;
        File file = new File(regionsFolder, e.getPlayer().getWorld().getName());
        file.mkdirs();
        worlds.put(e.getPlayer().getWorld().getUID(), new WorldLandManager(e.getPlayer().getWorld(), plugin, file));
    }
}
