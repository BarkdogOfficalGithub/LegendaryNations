package org.dragonet.bukkit.lnations.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.dragonet.bukkit.lnations.data.nation.NationFlag;

/**
 * Created on 2017/11/28.
 */
public class InvincibleFlagListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerDamage(EntityDamageEvent e){
        if(!Player.class.isAssignableFrom(e.getEntity().getClass())) return;
        if(EventHelper.hasFlagSet(e.getEntity().getWorld(), e.getEntity().getLocation().getChunk(), NationFlag.INVINCIBLE)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerExaust(FoodLevelChangeEvent e){
        if(!Player.class.isAssignableFrom(e.getEntity().getClass())) return;
        if(EventHelper.hasFlagSet(e.getEntity().getWorld(), e.getEntity().getLocation().getChunk(), NationFlag.INVINCIBLE)) {
            e.setCancelled(true);
        }
    }

}
