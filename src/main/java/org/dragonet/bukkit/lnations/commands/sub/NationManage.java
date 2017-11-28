package org.dragonet.bukkit.lnations.commands.sub;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.dragonet.bukkit.lnations.Lang;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.commands.NationSubCommand;
import org.dragonet.bukkit.lnations.data.nation.Nation;
import org.dragonet.bukkit.lnations.data.nation.NationPermission;
import org.dragonet.bukkit.menuapi.ItemMenuInstance;

import java.util.List;

/**
 * Created on 2017/11/18.
 */
public class NationManage implements NationSubCommand {
    @Override
    public void run(Player player, String[] args) {
        // first we gotta choose which nation to claim for
        List<Nation> nations = LegendaryNationsPlugin.getInstance().getPlayerManager().getNations(player);
        if(nations.size() <= 0) {
            Lang.sendMessage(player, "land.no-nation");
            return;
        }
        ItemMenuInstance menuInstance = new ItemMenuInstance(Lang.build("manage.gui.choose-nation"), nations.size());
        for(int i = 0; i < nations.size(); i++) {
            Nation n = nations.get(i);
            menuInstance.setButton(i, n.getIcon(), n.getName(), ((humanEntity, itemMenuInstance) -> {
                player.closeInventory();
                openManagerMenu(player, n);
            }));
        }
        LegendaryNationsPlugin.getInstance().getMenus().open(player, menuInstance);
    }

    public void openManagerMenu(Player player, Nation nation) {
        ItemMenuInstance menu = new ItemMenuInstance(Lang.build("manage.gui.manage", nation.getName()), 9);
        if(nation.hasPermission(player, NationPermission.CHANGE_DISPLAY_NAME)) {
            menu.setButton(0, Material.PAPER, Lang.build("manage.gui.options.change-display-name.button"), Lang.getStringList("manage.gui.options.change-display-name.lore"), ((humanEntity, itemMenuInstance) -> {
                player.closeInventory();
                Lang.sendMessageList(player, "manage.gui.options.change-display-name.lore");
            }));
        } else {
            menu.setButton(0, Material.PAPER, Lang.build("manage.gui.options.change-display-name.button") + Lang.build("manage.no-permission-button-suffix"), Lang.getStringList("manage.gui.options.change-display-name.lore"), ((humanEntity, itemMenuInstance) -> {
                Lang.sendMessage(player, "manage.no-permission");
            }));
        }
        if(nation.hasPermission(player, NationPermission.CHANGE_ICON)) {
            menu.setButton(1, Material.ITEM_FRAME, Lang.build("manage.gui.options.change-icon.button"), Lang.getStringList("manage.gui.options.change-icon.lore"), ((humanEntity, itemMenuInstance) -> {
                player.closeInventory();

            }));
        } else {
            menu.setButton(1, Material.ITEM_FRAME, Lang.build("manage.gui.options.change-icon.button") + Lang.build("manage.no-permission-button-suffix"), Lang.getStringList("manage.gui.options.change-icon.lore"), ((humanEntity, itemMenuInstance) -> {
                Lang.sendMessage(player, "manage.no-permission");
            }));
        }
        if(nation.hasPermission(player, NationPermission.MANAGE_LAND)) {
            menu.setButton(2, Material.FENCE, Lang.build("manage.gui.options.manage-land.button"), Lang.getStringList("manage.gui.options.manage-land.lore"), ((humanEntity, itemMenuInstance) -> {
                player.closeInventory();
                NationLand.openLandMenu(player, nation, player.getWorld());
            }));
        } else {
            menu.setButton(2, Material.FENCE, Lang.build("manage.gui.options.manage-land.button") + Lang.build("manage.no-permission-button-suffix"), Lang.getStringList("manage.gui.options.manage-land.lore"), ((humanEntity, itemMenuInstance) -> {
                player.closeInventory();
                Lang.sendMessage(player, "manage.no-permission");
            }));
        }
        if(nation.hasPermission(player, NationPermission.MANAGE_FLAGS)) {
            menu.setButton(4, Material.BOOK_AND_QUILL, Lang.build("manage.gui.options.flags.button"), Lang.getStringList("manage.gui.options.flags.lore"), ((humanEntity, itemMenuInstance) -> {
                player.closeInventory();
                NationFlags.openFlagsMenu(player, nation);
            }));
        } else {
            menu.setButton(4, Material.FENCE, Lang.build("manage.gui.options.flags.button") + Lang.build("manage.no-permission-button-suffix"), Lang.getStringList("manage.gui.options.flags.lore"), ((humanEntity, itemMenuInstance) -> {
                player.closeInventory();
                Lang.sendMessage(player, "manage.no-permission");
            }));
        }
        if(nation.hasPermission(player, NationPermission.MANAGE_PERMISSIONS)) {
            menu.setButton(4, Material.BOOK_AND_QUILL, Lang.build("manage.gui.options.gperms-public.button"), Lang.getStringList("manage.gui.options.gperms-public.lore"), ((humanEntity, itemMenuInstance) -> {
                player.closeInventory();
                NationGPerms.openPermissionMenu(player, "public", nation);
            }));
        } else {
            menu.setButton(4, Material.FENCE, Lang.build("manage.gui.options.gperms-public.button") + Lang.build("manage.no-permission-button-suffix"), Lang.getStringList("manage.gui.options.gperms-public.lore"), ((humanEntity, itemMenuInstance) -> {
                player.closeInventory();
                Lang.sendMessage(player, "manage.no-permission");
            }));
        }
        if(nation.hasPermission(player, NationPermission.MANAGE_PERMISSIONS)) {
            menu.setButton(5, Material.BOOK_AND_QUILL, Lang.build("manage.gui.options.gperms-member.button"), Lang.getStringList("manage.gui.options.gperms-member.lore"), ((humanEntity, itemMenuInstance) -> {
                player.closeInventory();
                NationGPerms.openPermissionMenu(player, "member", nation);
            }));
        } else {
            menu.setButton(5, Material.FENCE, Lang.build("manage.gui.options.gperms-member.button") + Lang.build("manage.no-permission-button-suffix"), Lang.getStringList("manage.gui.options.gperms-member.lore"), ((humanEntity, itemMenuInstance) -> {
                player.closeInventory();
                Lang.sendMessage(player, "manage.no-permission");
            }));
        }
        LegendaryNationsPlugin.getInstance().getMenus().open(player, menu);
    }
}
