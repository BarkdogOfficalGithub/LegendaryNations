package org.dragonet.bukkit.lnations.commands.sub;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.dragonet.bukkit.lnations.Lang;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.commands.NationSubCommand;
import org.dragonet.bukkit.lnations.data.nation.Nation;
import org.dragonet.bukkit.lnations.data.nation.NationPermission;

import java.util.UUID;

/**
 * Created on 2017/11/19.
 */
public class NationMemberCommand implements NationSubCommand {
    @Override
    public void run(Player player, String[] args) {
        if(args.length != 3) {
            Lang.sendMessage(player, "usage.member");
            return;
        }
        Nation n = LegendaryNationsPlugin.getInstance().getNationManager().getNation(args[0]);
        if(n == null) {
            Lang.sendMessage(player, "member.nation-not-found");
            return;
        }
        if(!n.hasPermission(player, NationPermission.MANAGE_MEMBERS)) {
            Lang.sendMessage(player, "member.no-permission");
            return;
        }
        OfflinePlayer target = LegendaryNationsPlugin.getInstance().getServer().getPlayer(args[2]);
        if(target == null) {
            UUID uuid = LegendaryNationsPlugin.getInstance().getPlayerManager().tryGetUUID(args[2]);
            if(uuid != null) {
                target = LegendaryNationsPlugin.getInstance().getServer().getOfflinePlayer(uuid);
            }
        }
        if(target == null) {
            Lang.sendMessage(player, "member.player-not-found");
            return;
        }
        String action = args[1].toLowerCase();
        if(!action.equals("add") && !action.equals("remove")) {
            Lang.sendMessage(player, "member.wrong-action");
            return;
        }
        if(action.equals("add")) {
            if(n.isMember(target)) {
                Lang.sendMessage(player, "member.add.already-exists", target.getName());
            } else {
                boolean succ = n.addMember(target);
                if(succ) {
                    Lang.sendMessage(player, "member.add.success", target.getName(), n.getDisplayName(), n.getName());
                    if(target.isOnline()) {
                        Lang.sendMessage(target, "member.add.target", n.getDisplayName(), n.getName());
                    }
                } else {
                    Lang.sendMessage(player, "member.add.failed");
                }
            }
        } else {
            if(!n.isMember(target)) {
                Lang.sendMessage(player, "member.remove.not-member", target.getName());
            } else {
                boolean succ = n.removeMember(target);
                if(succ) {
                    Lang.sendMessage(player, "member.remove.success", target.getName(), n.getDisplayName(), n.getName());
                    if(target.isOnline()) {
                        Lang.sendMessage(target, "member.remove.target", n.getDisplayName(), n.getName());
                    }
                } else {
                    Lang.sendMessage(player, "member.remove.failed");
                }
            }
        }
    }
}
