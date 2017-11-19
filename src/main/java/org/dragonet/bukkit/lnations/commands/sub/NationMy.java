package org.dragonet.bukkit.lnations.commands.sub;

import org.bukkit.entity.Player;
import org.dragonet.bukkit.lnations.Lang;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.commands.NationSubCommand;
import org.dragonet.bukkit.lnations.data.nation.Nation;

import java.util.List;

/**
 * Created on 2017/11/18.
 */
public class NationMy implements NationSubCommand {
    @Override
    public void run(Player player, String[] args) {
        Lang.sendMessage(player, "my.belong-to");
        List<String> nation_names = LegendaryNationsPlugin.getInstance().getPlayerManager().getNationNames(player);

        String message = "";
        for(String name : nation_names) {
            Nation n = LegendaryNationsPlugin.getInstance().getNationManager().getNation(name);
            if(n == null /*|| !n.isMember(player)*/) {
                LegendaryNationsPlugin.getInstance().getPlayerManager().removeFromNation(player, name);
                continue;
            }
            message += Lang.build(n.getLeader().equals(player.getUniqueId()) ? "my.as-leader" : "my.as-member", n.getName());
        }
        player.sendMessage(message);
    }
}
