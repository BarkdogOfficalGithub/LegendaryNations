package org.dragonet.bukkit.lnations.data.land;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2017/11/17.
 */
public class WorldLandManager {

    private final World world;

    private final LegendaryNationsPlugin plugin;
    private final File regionsFolder;

    private final Map<Integer, Map<Integer, CachedRegion>> regions = new HashMap<>();

    public WorldLandManager(World world, LegendaryNationsPlugin plugin, File regionsFolder) {
        this.world = world;
        this.plugin = plugin;
        this.regionsFolder = regionsFolder;
    }

    /**
     * Gets the nation where it belongs to
     * @param chunkX
     * @param chunkZ
     * @return
     */
    public String getNationName(int chunkX, int chunkZ) {
        int regionX = chunkX >> 8;
        int regionZ = chunkZ >> 8;
        if(!regions.containsKey(regionX) || !regions.get(regionX).containsKey(regionZ)) {
            // not loaded? we gotta do it manually
            File regionFile = getRegionFile(regionX, regionZ);
            if(!regionFile.exists()) return null;
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(regionFile);
            String key = chunkKey(chunkX, chunkZ);
            if(!yaml.contains(key)) return null;
            return yaml.getString(key);
        } else {
            return regions.get(regionX).get(regionZ).getChunkNation(chunkX, chunkZ);
        }
    }

    /**
     *
     * @param chunkX
     * @param chunkZ
     * @param name
     * @return true when success and made change, false when conflicts or error
     */
    public boolean claimLand(int chunkX, int chunkZ, String name) {
        if(getNationName(chunkX, chunkZ) != null) return false;
        int regionX = chunkX >> 8;
        int regionZ = chunkZ >> 8;
        if(!regions.containsKey(regionX) || !regions.get(regionX).containsKey(regionZ)) {
            File regionFile = getRegionFile(regionX, regionZ);
            if(!regionFile.exists()) {
                try {
                    regionFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(regionFile);
            String key = chunkKey(chunkX, chunkZ);
            if(configuration.contains(key)) return false;
            configuration.set(key, name.toLowerCase());
            try {
                configuration.save(regionFile);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            plugin.getNationManager().getNation(name).claimLand(world, chunkX, chunkZ);
            return true;
        } else {
            boolean status = regions.get(regionX).get(regionZ).claimLand(chunkX, chunkZ, name);
            if(status) {
                plugin.getNationManager().getNation(name).claimLand(world, chunkX, chunkZ);
            }
            return status;
        }
    }

    /**
     * un-claims a lnations
     * @param chunkX
     * @param chunkZ
     * @return nation's name when success, null when no changes or error
     */
    public String unclaimLand(int chunkX, int chunkZ) {
        if(getNationName(chunkX, chunkZ) == null) return null;
        int regionX = chunkX >> 8;
        int regionZ = chunkZ >> 8;
        if(!regions.containsKey(regionX) || !regions.get(regionX).containsKey(regionZ)) {
            File regionFile = getRegionFile(regionX, regionZ);
            if (!regionFile.exists()) {
                return null;
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(regionFile);
            String key = chunkKey(chunkX, chunkZ);
            if(!config.contains(key)) return null;
            String nation = config.getString(key);
            config.set(key, null);
            if(config.getKeys(false).size() <= 0) {
                regionFile.delete();
                return nation;
            }
            try {
                config.save(regionFile);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            plugin.getNationManager().getNation(nation).unclaimLand(world, chunkX, chunkZ);
            return nation;
        } else {
            String nation = regions.get(regionX).get(regionZ).unclaimLand(chunkX, chunkZ);
            if(nation != null) {
                plugin.getNationManager().getNation(nation).unclaimLand(world, chunkX, chunkZ);
            }
            return nation;
        }
    }

    /**
     * called when chunk loads, we load that region
     * @param chunk
     */
    public void onChunkLoad(Chunk chunk) {
        int regionX = chunk.getX() >> 8;
        int regionZ = chunk.getZ() >> 8;
        if(!regions.containsKey(regionX)) {
            regions.put(regionX, new HashMap<>());
        }
        CachedRegion cache;
        if(!regions.get(regionX).containsKey(regionZ)) {
            File f = getRegionFile(regionX, regionZ);
            if (!f.exists()) try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cache = new CachedRegion(YamlConfiguration.loadConfiguration(f));
            regions.get(regionX).put(regionZ, cache);
        } else {
            cache = regions.get(regionX).get(regionZ);
        }
        cache.lockChunk(chunk);
    }

    /**
     * called when chunk unloads, unload the region file if no more chunks used
     * @param chunk
     */
    public void onChunkUnload(Chunk chunk) {
        int regionX = chunk.getX() >> 8;
        int regionZ = chunk.getZ() >> 8;
        if(!regions.containsKey(regionX) || !regions.get(regionX).containsKey(regionZ)) {
            plugin.getLogger().warning("Strange, chunk not managed? ");
            return;
        }
        CachedRegion cache = regions.get(regionX).get(regionZ);
        cache.unlockChunk(chunk);
        if(cache.areLocksEmpty()) {
            // we can unload it now
            YamlConfiguration regionConfig = cache.config;
            plugin.getLogger().info(String.format("Saving and unloading region (%d, %d)", regionX, regionZ));
            File file = getRegionFile(regionX, regionZ);
            if(regionConfig.getKeys(false).size() <= 0) {
                file.delete();
            } else {
                try {
                    regionConfig.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            regions.get(regionX).remove(regionZ);
            if(regions.get(regionX).size() <= 0) {
                regions.remove(regionX);
            }
        }
    }

    public void cleanAndSave() {
        regions.entrySet().forEach((entryX) -> {
            int regionX = entryX.getKey();
            entryX.getValue().entrySet().forEach((entryZ) -> {
                int regionZ = entryZ.getKey();
                plugin.getLogger().info(String.format("Saving and unloading region (%d, %d), clean and save", regionX, regionZ));
                File file = getRegionFile(regionX, regionZ);
                if(entryZ.getValue().config.getKeys(false).size() <= 0) {
                    file.delete();
                } else {
                    try {
                        entryZ.getValue().config.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    public File getRegionFile(int regionX, int regionZ) {
        return new File(regionsFolder, String.format("r.%d.%d.yml", regionX, regionZ));
    }

    public static String chunkKey(int chunkX, int chunkZ) {
        return String.format("chunk-%d-%d", chunkX, chunkZ);
    }
}
