package org.dragonet.bukkit.lnations;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.dragonet.bukkit.lnations.land.LandManager;
import org.dragonet.bukkit.lnations.nation.NationManager;
import org.dragonet.bukkit.lnations.player.PlayerManager;

import java.io.File;

/**
 * Created on 2017/11/17.
 */
public class LegendaryNationsPlugin extends JavaPlugin {

    private static LegendaryNationsPlugin instance;

    public static LegendaryNationsPlugin getInstance() {
        return instance;
    }

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


        economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();

        landManager = new LandManager(this);
        nationManager = new NationManager(this);
        playerManager = new PlayerManager(this);

        getServer().getPluginManager().registerEvents(landManager, this);
        getServer().getScheduler().runTaskTimer(this, nationManager, 12000L, 36000L); // clean up task, 10min delay, 60min/time
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    @Override
    public YamlConfiguration getConfig() {
        return config;
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
}
