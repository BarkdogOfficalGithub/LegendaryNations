package org.dragonet.bukkit.lnations.commands.sub;

import org.bukkit.entity.Player;
import org.dragonet.bukkit.lnations.Lang;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.commands.NationSubCommand;
import org.dragonet.bukkit.lnations.data.nation.Nation;
import org.dragonet.bukkit.lnations.data.nation.NationPermission;

/**
 * Created on 2017/11/28.
 */
public class NationDisplay implements NationSubCommand {
    @Override
    public void run(Player player, String[] args) {
        if(args.length < 2) {
            Lang.sendMessage(player, "usage.display");
            return;
        }
        Nation nation = LegendaryNationsPlugin.getInstance().getNationManager().getNation(args[0]);
        if(nation == null) {
            Lang.sendMessageList(player, "display.not-found");
            return;
        }
        if(!nation.hasPermission(player, NationPermission.CHANGE_DISPLAY_NAME)) {
            Lang.sendMessage(player, "display.no-permission");
            return;
        }
        StringBuilder new_display_name = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            new_display_name.append(args[i]);
            if(i < args.length - 1) new_display_name.append(" ");
        }
        nation.setDisplayName(new_display_name.toString());
        nation.getAllOnlineMembersAsPlayers().forEach(c ->
            Lang.sendMessage(c, "display.updated", nation.getName(), new_display_name, player.getName())
        );
    }
}
