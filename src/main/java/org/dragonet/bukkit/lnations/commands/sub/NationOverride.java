package org.dragonet.bukkit.lnations.commands.sub;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.dragonet.bukkit.lnations.Lang;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.commands.NationSubCommand;

/**
 * Created on 2017/11/20.
 */
public class NationOverride implements NationSubCommand {
    @Override
    public void run(Player player, String[] args) {
        if(!player.hasPermission("legendarynations.admin")) {
            Lang.sendMessage(player, "override.no-permission");
            return;
        }
        if(player.hasMetadata(LegendaryNationsPlugin.OVERRIDE_MODE_METADATA)) {
            player.removeMetadata(LegendaryNationsPlugin.OVERRIDE_MODE_METADATA, LegendaryNationsPlugin.getInstance());
            Lang.sendMessage(player, "override.turned-off");
        } else {
            player.setMetadata(LegendaryNationsPlugin.OVERRIDE_MODE_METADATA, new FixedMetadataValue(LegendaryNationsPlugin.getInstance(), true));
            Lang.sendMessage(player, "override.turned-on");
        }
    }
}
