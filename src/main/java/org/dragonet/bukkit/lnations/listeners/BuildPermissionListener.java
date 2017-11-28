package org.dragonet.bukkit.lnations.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.data.land.WorldLandManager;
import org.dragonet.bukkit.lnations.data.nation.Nation;
import org.dragonet.bukkit.lnations.data.nation.NationPermission;

import static org.dragonet.bukkit.lnations.listeners.EventHelper.shouldCancel;

/**
 * Created on 2017/11/20.
 */
public class BuildPermissionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlace(BlockPlaceEvent e) {
        if(shouldCancel(e.getPlayer(), e.getBlock().getWorld(), e.getBlock().getChunk(), NationPermission.BUILD)) {
            e.setCancelled(true);
            e.setBuild(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlace(BlockMultiPlaceEvent e) {
        if(shouldCancel(e.getPlayer(), e.getBlock().getWorld(), e.getBlock().getChunk(), NationPermission.BUILD)) {
            e.setCancelled(true);
            e.setBuild(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBreak(BlockBreakEvent e) {
        if(shouldCancel(e.getPlayer(), e.getBlock().getWorld(), e.getBlock().getChunk(), NationPermission.BUILD)) {
            e.setCancelled(true);
            e.setDropItems(false);
        }
    }

    /**
     * Denies cross-nation piston movements
     * @param e
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPiston(BlockPistonExtendEvent e) {
        WorldLandManager wlm = LegendaryNationsPlugin.getInstance().getLandManager().getWorldManager(e.getBlock().getWorld());
        Nation fromNation = wlm.getNationAt(e.getBlock().getChunk().getX(), e.getBlock().getChunk().getZ());
        for(Block affected : e.getBlocks()) {
            Nation target = wlm
                    .getNationAt(affected.getChunk().getX(), affected.getChunk().getZ());
            if(target != null && !target.getName().equals(fromNation.getName())) {
                // do not allow cross-nation piston movements!
                e.setCancelled(true);
                return;
            }
        }
    }
}
