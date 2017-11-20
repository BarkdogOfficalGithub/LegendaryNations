package org.dragonet.bukkit.lnations.listeners;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.dragonet.bukkit.lnations.Lang;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.data.land.WorldLandManager;
import org.dragonet.bukkit.lnations.data.nation.Nation;
import org.dragonet.bukkit.lnations.data.nation.NationPermission;

/**
 * Created on 2017/11/20.
 */
public class EventHelper {

    /**
     *
     * @param player
     * @param world
     * @param chunk
     * @param permission
     * @return true if should be cancelled
     */
    public static boolean shouldCancel(Player player, World world, Chunk chunk, NationPermission permission) {
        // override mode
        if(LegendaryNationsPlugin.isInOverrideMode(player)) return false;

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        WorldLandManager wlm = LegendaryNationsPlugin.getInstance().getLandManager().getWorldManager(world);
        Nation n = wlm.getNationAt(chunkX, chunkZ);
        if(n == null) return false;
        if(n.hasPermission(player, permission)) return false;
        Lang.sendMessage(player, "no-permission-messages." + permission.name(), n.getDisplayName(), n.getName());
        return true;
    }

}
