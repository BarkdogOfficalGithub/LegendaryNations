package org.dragonet.bukkit.lnations.listeners;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.dragonet.bukkit.lnations.data.nation.NationFlag;

/**
 * Created on 2017/11/28.
 */
public class CreatureFlagsListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onCreatureSpawn(CreatureSpawnEvent e) {
        if(e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) return; // admins can do whatever they want
        if ((Monster.class.isAssignableFrom(e.getEntity().getClass()) && EventHelper.hasFlagSet(e.getEntity().getWorld(), e.getEntity().getLocation().getChunk(), NationFlag.NO_MONSTER_SPAWNING)) ||
                (Animals.class.isAssignableFrom(e.getEntity().getClass()) && EventHelper.hasFlagSet(e.getEntity().getWorld(), e.getEntity().getLocation().getChunk(), NationFlag.NO_ANIMAL_SPAWNING))) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onMonsterGrief(EntityChangeBlockEvent e){
        if (EventHelper.hasFlagSet(e.getEntity().getWorld(), e.getEntity().getLocation().getChunk(), NationFlag.NO_MONSTER_GRIEF)) {
            e.setCancelled(true);
        }
    }

}
