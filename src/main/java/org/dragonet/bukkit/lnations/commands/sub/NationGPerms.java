package org.dragonet.bukkit.lnations.commands.sub;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.dragonet.bukkit.lnations.Lang;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.commands.NationSubCommand;
import org.dragonet.bukkit.lnations.data.nation.Nation;
import org.dragonet.bukkit.lnations.data.nation.NationPermission;
import org.dragonet.bukkit.menuapi.ItemMenu;
import org.dragonet.bukkit.menuapi.ItemMenuInstance;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created on 2017/11/20.
 */
public class NationGPerms implements NationSubCommand {
    @Override
    public void run(Player player, String[] args) {
        // first we gotta choose which nation to claim for
        List<Nation> nations = LegendaryNationsPlugin.getInstance().getPlayerManager().getNationsWithPermission(player, NationPermission.MANAGE_PERMISSIONS);
        if(nations.size() <= 0) {
            Lang.sendMessage(player, "g-perm.no-nation");
            return;
        }
        ItemMenuInstance menuInstance = new ItemMenuInstance(Lang.build("g-perm.gui.choose-nation"), nations.size() + 9);
        ItemMenuWithMode menu = new ItemMenuWithMode(menuInstance, "member");
        updateButtons(menu);
        for(int i = 0; i < nations.size(); i++) {
            Nation n = nations.get(i);
            if(n.hasPermission(player, NationPermission.MANAGE_LAND)) {
                menuInstance.setButton(i + 9, n.getIcon(), n.getName(), Arrays.asList("\u00a73" + n.getDisplayName()), ((humanEntity, itemMenuInstance) -> {
                    player.closeInventory();
                    openPermissionMenu(player, menu.mode, n);
                }));
            }
        }
        LegendaryNationsPlugin.getInstance().getMenus().open(player, menuInstance);
    }

    private void openPermissionMenu(Player player, String mode, Nation nation) {
        Set<NationPermission> perms = nation.editGeneralPermission(mode);
        ItemMenuInstance menu = new ItemMenuInstance(Lang.build("g-perm.gui.editor-title-" + mode.toLowerCase()), NationPermission.values().length);
        for(int i = 0; i < NationPermission.values().length; i++) {
            NationPermission p = NationPermission.values()[i];
            menu.setButton(i, perms.contains(p) ? Material.ENCHANTED_BOOK : Material.BOOK,
                    Lang.build("permissions." + p.name()) + (perms.contains(p) ? " " + Lang.build("g-perm.gui.allowed") : ""),
                    Lang.getStringList("permissions-lore." + p.name()),
                    ((humanEntity, itemMenuInstance) -> {
                        if(perms.contains(p)) {
                            perms.remove(p);
                        } else {
                            perms.add(p);
                        }
                        openPermissionMenu(player, mode, nation);
                    }));
        }
        LegendaryNationsPlugin.getInstance().getMenus().open(player, menu);
    }

    private void updateButtons(ItemMenuWithMode menu) {
        for(int i : Arrays.asList(0, 1, 3, 4, 5, 7, 8)) {
            ItemMenu.MenuItemHandler emptyHandler = (humanEntity, itemMenuInstance) -> {};
            menu.instance.setButton(i, Material.STAINED_GLASS_PANE, "", emptyHandler);
        }
        menu.instance.setButton(2,
                menu.mode.equalsIgnoreCase("member") ? Material.ENCHANTED_BOOK : Material.BOOK,
                (menu.mode.equalsIgnoreCase("member") ? Lang.build("g-perm.gui.active-mode") : "") + Lang.build("g-perm.gui.member-mode"),
                Lang.getStringList("g-perm.gui.member-mode-lore"),
                ((humanEntity, itemMenuInstance) -> {
                    if(!menu.mode.equals("member")) {
                        menu.mode = "member";
                        updateButtons(menu);
                    }
                }));
        menu.instance.setButton(6,
                menu.mode.equalsIgnoreCase("public") ? Material.ENCHANTED_BOOK : Material.BOOK,
                (menu.mode.equalsIgnoreCase("public") ? Lang.build("g-perm.gui.active-mode") : "") + Lang.build("g-perm.gui.public-mode"),
                Lang.getStringList("g-perm.gui.public-mode-lore"),
                ((humanEntity, itemMenuInstance) -> {
                    if(!menu.mode.equals("public")) {
                        menu.mode = "public";
                        updateButtons(menu);
                    }
                }));
    }

    public static class ItemMenuWithMode {
        public final ItemMenuInstance instance;

        public String mode;

        public ItemMenuWithMode(ItemMenuInstance instance, String mode) {
            this.instance = instance;
            this.mode = mode;
        }
    }
}
