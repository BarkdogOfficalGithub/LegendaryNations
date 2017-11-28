package org.dragonet.bukkit.lnations.listeners;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.dragonet.bukkit.lnations.Lang;
import org.dragonet.bukkit.lnations.data.nation.NationFlag;
import org.dragonet.bukkit.lnations.data.nation.NationPermission;

import static org.dragonet.bukkit.lnations.listeners.EventHelper.shouldCancel;

/**
 * Created on 2017/11/20.
 */
public class InteractionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onInteract(PlayerInteractEvent e) {
        if(e.getAction().name().endsWith("_BLOCK") || e.getAction().equals(Action.PHYSICAL)) {
            if (shouldCancel(e.getPlayer(), e.getPlayer().getWorld(), e.getPlayer().getLocation().getChunk(), NationPermission.INTERACT)) {
                e.setUseInteractedBlock(Event.Result.DENY);
                e.setUseItemInHand(Event.Result.DENY);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onAnimalHurt(EntityDamageByEntityEvent e){
        if(!Player.class.isAssignableFrom(e.getDamager().getClass())) return;
        if(!Animals.class.isAssignableFrom(e.getEntity().getClass())) return;
        if (shouldCancel((Player) e.getDamager(), e.getDamager().getWorld(), e.getDamager().getLocation().getChunk(), NationPermission.HURT_ANIMALS)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPvP(EntityDamageByEntityEvent e) {
        if(!Player.class.isAssignableFrom(e.getEntity().getClass())) return;
        if(!Player.class.isAssignableFrom(e.getDamager().getClass())) return;
        if(!EventHelper.hasFlagSet(e.getEntity().getWorld(), e.getEntity().getLocation().getChunk(), NationFlag.PVP)) {
            Lang.sendMessage(e.getDamager(), "flag-messages.PVP");
        }
    }

}
