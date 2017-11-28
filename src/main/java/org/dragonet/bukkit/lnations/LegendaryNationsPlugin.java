package org.dragonet.bukkit.lnations;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.dragonet.bukkit.lnations.commands.NationCommand;
import org.dragonet.bukkit.lnations.data.land.LandManager;
import org.dragonet.bukkit.lnations.data.nation.NationManager;
import org.dragonet.bukkit.lnations.data.player.PlayerManager;
import org.dragonet.bukkit.lnations.listeners.BuildPermissionListener;
import org.dragonet.bukkit.lnations.listeners.InteractionListener;
import org.dragonet.bukkit.lnations.listeners.CreatureFlagsListener;
import org.dragonet.bukkit.lnations.listeners.InvincibleFlagListener;
import org.dragonet.bukkit.menuapi.ItemMenu;
import org.dragonet.bukkit.menuapi.MenuAPIPlugin;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created on 2017/11/17.
 */
public class LegendaryNationsPlugin extends JavaPlugin {

    public final static String OVERRIDE_MODE_METADATA = "legendary-nations-override";

    public final static Pattern NATION_NAME_REGEX = Pattern.compile("^[0-9a-zA-Z_]+$");

    private static LegendaryNationsPlugin instance;

    public static LegendaryNationsPlugin getInstance() {
        return instance;
    }

    private ItemMenu menus;
    private Economy economy;

    private YamlConfiguration config;

    private LandManager landManager;
    private NationManager nationManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        instance = this;
        saveResource("config.yml", false);
        saveResource("lang.yml", false);
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        Lang.lang = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "lang.yml"));

        menus = ((MenuAPIPlugin)getServer().getPluginManager().getPlugin("MenuAPI")).getMenus();
        economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();

        landManager = new LandManager(this);
        nationManager = new NationManager(this);
        playerManager = new PlayerManager(this);

        getServer().getPluginManager().registerEvents(playerManager, this);
        getServer().getPluginManager().registerEvents(landManager, this);
        getServer().getScheduler().runTaskTimer(this, nationManager, 20*60*5L, 20*60*5L); // clean up task, 10min delay, 60min/time

        landManager.init();

        // listeners
        getServer().getPluginManager().registerEvents(new BuildPermissionListener(), this);
        getServer().getPluginManager().registerEvents(new InteractionListener(), this);
        getServer().getPluginManager().registerEvents(new CreatureFlagsListener(), this);
        getServer().getPluginManager().registerEvents(new InvincibleFlagListener(), this);

        // finally, register the command
        getCommand("nation").setExecutor(new NationCommand(this));
    }

    @Override
    public void onDisable() {
        nationManager.saveAndClear();
        landManager.saveAndClear();

        instance = null;
    }

    @Override
    public YamlConfiguration getConfig() {
        return config;
    }

    public ItemMenu getMenus() {
        return menus;
    }

    public Economy getEconomy() {
        return economy;
    }

    public LandManager getLandManager() {
        return landManager;
    }

    public NationManager getNationManager() {
        return nationManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static boolean isInOverrideMode(HumanEntity player) {
        return player.hasMetadata(OVERRIDE_MODE_METADATA);
    }
}
