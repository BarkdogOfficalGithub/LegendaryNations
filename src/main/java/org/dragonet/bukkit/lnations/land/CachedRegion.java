package org.dragonet.bukkit.lnations.land;

import org.bukkit.Chunk;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created on 2017/11/17.
 */
public class CachedRegion {

    public final YamlConfiguration config;

    private Map<Integer, Set<Integer>> locks = new HashMap<>();

    public CachedRegion(YamlConfiguration config) {
        this.config = config;
    }

    public void lockChunk(Chunk c) {
        if(!locks.containsKey(c.getX())) locks.put(c.getX(), new HashSet<>());
        locks.get(c.getX()).add(c.getZ());
    }

    public void unlockChunk(Chunk c) {
        if(!locks.containsKey(c.getX())) return;
        Set<Integer> zSet = locks.get(c.getX());
        zSet.remove(c.getZ());
        if(zSet.size() <= 0) {
            locks.remove(c.getX());
        }
        return;
    }

    public String getChunkNation(int chunkX, int chunkZ) {
        String key = WorldLandManager.chunkKey(chunkX, chunkZ);
        if(!config.contains(key)) return null;
        return config.getString(key);
    }

    public boolean claimLand(int chunkX, int chunkZ, String name) {
        String key = WorldLandManager.chunkKey(chunkX, chunkZ);
        if(config.contains(key)) return false;
        config.set(key, name.toLowerCase());
        return true;
    }

    public String unclaimLand(int chunkX, int chunkZ) {
        String key = WorldLandManager.chunkKey(chunkX, chunkZ);
        if(!config.contains(key)) return null;
        String nation = config.getString(key);
        config.set(key, null);
        return nation;
    }

    public boolean areLocksEmpty() {
        return locks.size() <= 0;
    }
}
