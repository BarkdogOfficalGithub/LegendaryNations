package org.dragonet.bukkit.lnations.commands.sub;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.dragonet.bukkit.lnations.Lang;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.commands.NationSubCommand;
import org.dragonet.bukkit.lnations.data.land.WorldLandManager;
import org.dragonet.bukkit.lnations.data.nation.Nation;
import org.dragonet.bukkit.lnations.data.nation.NationPermission;
import org.dragonet.bukkit.menuapi.ItemMenu;
import org.dragonet.bukkit.menuapi.ItemMenuInstance;

import java.util.List;

/**
 * Created on 2017/11/18.
 */
public class NationLand implements NationSubCommand {
    @Override
    public void run(Player player, String[] args) {
        // first we gotta choose which nation to claim for
        List<Nation> nations = LegendaryNationsPlugin.getInstance().getPlayerManager().getNations(player);
        if(nations.size() <= 0) {
            Lang.sendMessage(player, "land.no-nation");
            return;
        }
        ItemMenuInstance menuInstance = new ItemMenuInstance(Lang.build("land.gui.choose-nation"), nations.size());
        for(int i = 0; i < nations.size(); i++) {
            Nation n = nations.get(i);
            if(n.hasPermission(player, NationPermission.MANAGE_LAND)) {
                menuInstance.setButton(i, n.getIcon(), Lang.build("land.gui.nation-permitted", n.getName()), ((humanEntity, itemMenuInstance) -> {
                    openLandMenu(player, n, player.getWorld());
                }));
            } else {
                menuInstance.setButton(i, n.getIcon(), Lang.build("land.gui.nation-unavailable", n.getName()), ((humanEntity, itemMenuInstance) -> {
                    Lang.sendMessage(player, "lang.no-permission");
                }));
            }
        }
        LegendaryNationsPlugin.getInstance().getMenus().open(player, menuInstance);
    }

    public static void openLandMenu(Player player, Nation n, World world) {
        ItemMenuInstance gui = new ItemMenuInstance(Lang.build("land.gui.manage", n.getName()), 5*9);
        int playerChunkX = player.getLocation().getChunk().getX();
        int playerChunkZ = player.getLocation().getChunk().getZ();
        WorldLandManager wlm = LegendaryNationsPlugin.getInstance().getLandManager().getWorldManager(world);
        for(int cx = playerChunkX - 4; cx < playerChunkX + 5; cx++) {
            for(int cz = playerChunkZ - 2; cz < playerChunkZ + 3; cz++) {
                int guiX = cx + 4 - playerChunkX;
                int guiY = cz + 2 - playerChunkZ;
                int guiIndex = guiY * 9 + guiX;
                Nation landNation = wlm.getNationAt(cx, cz);
                if(landNation == null) {
                    gui.setButton(guiIndex, Material.DIRT, String.format("\u00a7f(%d,%d) ", cx, cz) + Lang.build("land.gui.wilderness"), Lang.getStringList("land.gui.claim-lore"),
                            new ClaimRequestHandler(player, n, world, cx, cz));
                } else {
                    gui.setButton(guiIndex, landNation.getIcon(), String.format("\u00a7f(%d,%d) \u00a76%s \u00a77(\u00a7c%s\u00a77)", cx, cz, landNation.getDisplayName(), landNation.getName()), ((humanEntity, itemMenuInstance) ->
                        Lang.sendMessage(player, "land.claim.duplicated")
                    ));
                }
            }
        }
        LegendaryNationsPlugin.getInstance().getMenus().open(player, gui);
    }

    private static class ClaimRequestHandler implements ItemMenu.MenuItemHandler {

        private final Player player;
        private final Nation nation;
        private final World world;
        private final int chunkX;
        private final int chunkZ;

        public ClaimRequestHandler(Player player, Nation nation, World world, int chunkX, int chunkZ) {
            this.player = player;
            this.nation = nation;
            this.world = world;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        @Override
        public void onClick(HumanEntity humanEntity, ItemMenuInstance itemMenuInstance) {
            if(LegendaryNationsPlugin.getInstance().getLandManager().getWorldManager(world).getNationName(chunkX, chunkZ) != null) {
                Lang.sendMessage(humanEntity, "land.claim.duplicated");
                return;
            }

            if(!LegendaryNationsPlugin.isInOverrideMode(humanEntity)) {
                // reduce money
                double cost = LegendaryNationsPlugin.getInstance().getConfig().getDouble("cost.land");
                EconomyResponse eco = LegendaryNationsPlugin.getInstance().getEconomy().withdrawPlayer(player, cost);
                if(!eco.transactionSuccess()) {
                    Lang.sendMessage(humanEntity, "insufficient-money", cost);
                    return;
                }
            } else {
                Lang.sendMessage(player, "override-notice");
            }

            WorldLandManager wlm = LegendaryNationsPlugin.getInstance().getLandManager().getWorldManager(world);
            wlm.claimLand(chunkX, chunkZ, nation.getName());

            Lang.sendMessage(player, "land.claim.success", chunkX, chunkZ, nation.getName());

            humanEntity.closeInventory();
            openLandMenu(player, nation, world);
        }
    }
}
