package org.dragonet.bukkit.lnations.commands.sub;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.dragonet.bukkit.lnations.Lang;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.commands.NationSubCommand;
import org.dragonet.bukkit.lnations.data.nation.Nation;
import org.dragonet.bukkit.lnations.data.nation.NationFlag;
import org.dragonet.bukkit.lnations.data.nation.NationPermission;
import org.dragonet.bukkit.menuapi.ItemMenu;
import org.dragonet.bukkit.menuapi.ItemMenuInstance;

import java.util.List;
import java.util.Set;

/**
 * Created on 2017/11/28.
 */
public class NationFlags implements NationSubCommand {
    @Override
    public void run(Player player, String[] args) {
        // first we gotta choose which nation to claim for
        List<Nation> nations = LegendaryNationsPlugin.getInstance().getPlayerManager().getNations(player);
        if(nations.size() <= 0) {
            Lang.sendMessage(player, "flags-cmd.no-nation");
            return;
        }
        ItemMenuInstance menuInstance = new ItemMenuInstance(Lang.build("flags-cmd.gui.choose-nation"), nations.size());
        for(int i = 0; i < nations.size(); i++) {
            Nation n = nations.get(i);
            if(n.hasPermission(player, NationPermission.MANAGE_LAND)) {
                menuInstance.setButton(i, n.getIcon(), Lang.build("flags-cmd.gui.nation-permitted", n.getName()), ((humanEntity, itemMenuInstance) -> {
                    openFlagsMenu(player, n);
                }));
            } else {
                menuInstance.setButton(i, n.getIcon(), Lang.build("flags-cmd.gui.nation-unavailable", n.getName()), ((humanEntity, itemMenuInstance) -> {
                    Lang.sendMessage(player, "lang.no-permission");
                }));
            }
        }
        LegendaryNationsPlugin.getInstance().getMenus().open(player, menuInstance);
    }

    public static void openFlagsMenu(Player player, Nation n) {
        ItemMenuInstance menu = new ItemMenuInstance(Lang.build("flags-cmd.gui.editor-title", n.getDisplayName()), NationFlag.values().length);
        Set<NationFlag> flags = n.editFlags();
        NationFlag[] all_flags = NationFlag.values();
        for(int i = 0; i < all_flags.length; i++) {
            final NationFlag current_flag = all_flags[i];
            menu.setButton(i, flags.contains(current_flag) ? Material.ENCHANTED_BOOK : Material.BOOK,
                    flags.contains(all_flags[i]) ? Lang.build("flags." + current_flag.name()) + Lang.build("flags-cmd.gui.enabled") : Lang.build("flags." + current_flag.name()),
                    Lang.getStringList("flags-lore." + current_flag.name()), ((humanEntity, itemMenuInstance) -> {
                        if(!LegendaryNationsPlugin.isInOverrideMode(player) &&
                                !player.hasPermission("legendarynations.flags." + current_flag.name().toLowerCase())) {
                            Lang.sendMessage(player, "flags-cmd.no-permission-server");
                            return;
                        }
                        if(flags.contains(current_flag)) {
                            flags.remove(current_flag);
                        } else {
                            flags.add(current_flag);
                        }
                        player.closeInventory();
                        openFlagsMenu(player, n);
                    }));
        }
        LegendaryNationsPlugin.getInstance().getMenus().open(player, menu);
    }
}
