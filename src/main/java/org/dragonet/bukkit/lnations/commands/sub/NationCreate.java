package org.dragonet.bukkit.lnations.commands.sub;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.dragonet.bukkit.lnations.Lang;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.commands.NationSubCommand;

/**
 * Created on 2017/11/18.
 */
public class NationCreate implements NationSubCommand {
    @Override
    public void run(Player player, String[] args) {

        if(args.length != 1) {
            Lang.sendMessage(player, "usage.create");
            return;
        }

        if(!LegendaryNationsPlugin.isInOverrideMode(player)) {
            double cost = LegendaryNationsPlugin.getInstance().getConfig().getDouble("cost.nation");
            EconomyResponse response = LegendaryNationsPlugin.getInstance().getEconomy().withdrawPlayer(player, cost);
            if (!response.transactionSuccess()) {
                Lang.sendMessage(player, "insufficient-money", cost);
                return;
            }
        } else {
            Lang.sendMessage(player, "override-notice");
        }

        String nationName = args[0];
        if(!LegendaryNationsPlugin.NATION_NAME_REGEX.matcher(nationName).matches()) {
            Lang.sendMessage(player, "creation.illegal-name");
            return;
        }

        LegendaryNationsPlugin.getInstance().getNationManager().createNation(nationName, player);

        Lang.sendMessageList(player, "creation.success");
    }
}
