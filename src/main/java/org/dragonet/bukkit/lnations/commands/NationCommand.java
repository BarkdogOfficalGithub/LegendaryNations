package org.dragonet.bukkit.lnations.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dragonet.bukkit.lnations.Lang;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;
import org.dragonet.bukkit.lnations.commands.sub.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017/11/18.
 */
public class NationCommand implements CommandExecutor {

    private final LegendaryNationsPlugin plugin;

    private final Map<String, NationSubCommand> commands = new HashMap<>();

    public NationCommand(LegendaryNationsPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        registerSubCommand(Arrays.asList("create", "c", "make"), new NationCreate());
        registerSubCommand(Arrays.asList("my", "me", "info", "nations", "list"), new NationMy());
        registerSubCommand(Arrays.asList("land", "lands", "claim", "unclaim", "l", "res"), new NationLand());
        registerSubCommand(Arrays.asList("manage", "man", "gui", "control"), new NationManage());
        registerSubCommand(Arrays.asList("member", "members", "mem", "m"), new NationMemberCommand());
        registerSubCommand(Arrays.asList("gperms", "gperm", "genperms", "gp"), new NationGPerms());
        registerSubCommand(Arrays.asList("override", "admin", "or", "ignore"), new NationOverride());
    }

    private void registerSubCommand(List<String> names, NationSubCommand cmd) {
        for(String name : names) {
            commands.put(name, cmd);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!Player.class.isAssignableFrom(sender.getClass())) return true;
        if(args.length <= 0) {
            Lang.sendMessageList(sender, "help");
            return true;
        }
        String action = args[0].toLowerCase();
        String[] actionArgs = Arrays.copyOfRange(args, 1, args.length);
        if(!commands.containsKey(action)) {
            Lang.sendMessageList(sender, "help");
            return true;
        }
        commands.get(action).run((Player) sender, actionArgs);
        return true;
    }
}
