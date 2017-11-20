package org.dragonet.bukkit.lnations.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.dragonet.bukkit.lnations.data.nation.NationPermission;

import static org.dragonet.bukkit.lnations.listeners.EventHelper.shouldCancel;

/**
 * Created on 2017/11/20.
 */
public class InteractPermissionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInteract(PlayerInteractEvent e) {
        if(e.getAction().name().endsWith("_BLOCK") || e.getAction().equals(Action.PHYSICAL)) {
            if (shouldCancel(e.getPlayer(), e.getPlayer().getWorld(), e.getPlayer().getLocation().getChunk(), NationPermission.INTERACT)) {
                e.setUseInteractedBlock(Event.Result.DENY);
                e.setUseItemInHand(Event.Result.DENY);
                e.setCancelled(true);
            }
        }
    }

}
