package org.dragonet.bukkit.lnations.data.nation;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.dragonet.bukkit.lnations.LegendaryNationsPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 2017/11/17.
 */
public class Nation {

    private final YamlConfiguration internalConfiguration;
    private final File internalFile;

    private String name;

    private Material icon;

    private UUID leader;

    /**
     * general public permissions override general member permissions
     */
    private final Set<NationPermission> generalPublicPermissions;

    /**
     * general member permissions, override all member's permissions
     */
    private final Set<NationPermission> generalMemberPermissions;

    private final Map<UUID, NationMember> members = new HashMap<>();

    private final ConfigurationSection claims;

    private boolean changed;
    public long last_access_time = System.currentTimeMillis();

    public Nation(File internalFile) {
        this.internalFile = internalFile;
        internalConfiguration = YamlConfiguration.loadConfiguration(internalFile);
        name = internalConfiguration.getString("name");
        icon = Material.valueOf(internalConfiguration.getString("icon"));
        leader = UUID.fromString(internalConfiguration.getString("leader"));
        generalPublicPermissions = new HashSet<>();
        generalMemberPermissions = new HashSet<>();
        generalPublicPermissions.addAll(internalConfiguration.getStringList("general-permissions.public").stream().map(NationPermission::valueOf).collect(Collectors.toList()));
        generalMemberPermissions.addAll(internalConfiguration.getStringList("general-permissions.member").stream().map(NationPermission::valueOf).collect(Collectors.toList()));
        for(String strUniqueId : internalConfiguration.getConfigurationSection("members").getKeys(false)) {
            NationMember member = new NationMember(this, internalConfiguration.getConfigurationSection("members." + strUniqueId), UUID.fromString(strUniqueId));
            members.put(UUID.fromString(strUniqueId), member);
        }
        claims = internalConfiguration.getConfigurationSection("claims");
    }

    public String getName() {
        last_access_time = System.currentTimeMillis();
        return name;
    }

    public void setName(String name) {
        this.name = name;
        markChanged();
    }

    public UUID getLeader() {
        last_access_time = System.currentTimeMillis();
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
        markChanged();
    }

    public Material getIcon() {
        last_access_time = System.currentTimeMillis();
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
        markChanged();
    }

    /**
     * check is leader or member
     * @param player
     * @return
     */
    public boolean isInNation(Player player) {
        last_access_time = System.currentTimeMillis();
        if(leader.equals(player.getUniqueId())) return true;
        return isMember(player);
    }

    /**
     * check is member? (not including leader)
     * @param player
     * @return
     */
    public boolean isMember(Player player) {
        last_access_time = System.currentTimeMillis();
        return members.containsKey(player.getUniqueId());
    }

    /**
     * adds a member to the nation
     * @param player
     * @return true when changes made, false when no change
     */
    public boolean addMember(Player player){
        last_access_time = System.currentTimeMillis();
        if(isMember(player)) return false;
        markChanged();
        internalConfiguration.set("members." + player.getUniqueId(), NationMember.initializeNationMember(player.getUniqueId()));
        NationMember member = new NationMember(this, internalConfiguration.getConfigurationSection("members." + player.getUniqueId()), player.getUniqueId());
        member.markChanged();
        members.put(player.getUniqueId(), member);
        LegendaryNationsPlugin.getInstance().getPlayerManager().addToNation(player, name);
        return true;
    }

    public boolean checkLand(World world, int chunkX, int chunkZ) {
        String key = claimKey(world, chunkX);
        if(!claims.contains(key)) return false;
        return claims.getIntegerList(key).contains(chunkZ);
    }

    /**
     * claims a lnations for this nation
     * @param world
     * @param chunkX
     * @param chunkZ
     * @return true when success, false when already claimed or error
     */
    public boolean claimLand(World world, int chunkX, int chunkZ) {
        last_access_time = System.currentTimeMillis();
        if(checkLand(world, chunkX, chunkZ)) return false;
        markChanged();
        List<Integer> zs = new LinkedList<>();
        String key = claimKey(world, chunkX);
        if(claims.contains(key)) {
            zs.addAll(claims.getIntegerList(key));
        }
        zs.add(chunkZ);
        claims.set(key, zs);
        LegendaryNationsPlugin.getInstance().getLandManager().getWorldManager(world).claimLand(chunkX, chunkZ, name.toLowerCase());
        return true;
    }

    /**
     * un-claims a lnations
     * @param world
     * @param chunkX
     * @param chunkZ
     * @return true when success, false when not claimed or error
     */
    public boolean unclaimLand(World world, int chunkX, int chunkZ) {
        last_access_time = System.currentTimeMillis();
        if(!checkLand(world, chunkX, chunkZ)) return false;
        markChanged();
        List<Integer> zs = new LinkedList<>();
        String key = claimKey(world, chunkX);
        if(claims.contains(key)) {
            zs.addAll(claims.getIntegerList(key));
        }
        zs.remove(chunkZ);
        if(zs.size() <= 0) {
            claims.set(key, null);
        } else {
            claims.set(key, zs);
        }
        LegendaryNationsPlugin.getInstance().getLandManager().getWorldManager(world).unclaimLand(chunkX, chunkZ);
        return true;
    }

    /**
     * removes a member
     * @param player
     * @return true when changes made, false when no change
     */
    public boolean removeMember(Player player) {
        last_access_time = System.currentTimeMillis();
        if(!isMember(player)) return false;
        markChanged();
        internalConfiguration.set("members." + player.getUniqueId(), null);
        members.remove(player.getUniqueId());
        LegendaryNationsPlugin.getInstance().getPlayerManager().removeFromNation(player, name);
        return true;
    }

    public boolean hasPermission(Player player, NationPermission permission) {
        last_access_time = System.currentTimeMillis();
        if(player.getUniqueId().equals(leader)) return true;
        if(generalPublicPermissions.contains(permission)) return true;
        if(!isMember(player)) return false;
        return members.get(player.getUniqueId()).hasPermission(permission);
    }

    public void markChanged() {
        last_access_time = System.currentTimeMillis();
        changed = true;
    }

    public boolean saveConfiguration() {
        if(!changed) {
            System.out.println("Not saving nation <" + name + "> because not changed! ");
            return true;
        }
        internalConfiguration.set("name", name);
        internalConfiguration.set("leader", leader.toString());
        members.values().forEach((c) -> {
            // this will update ConfigurationSection
            if(c.isChanged()) c.updateInternalConfiguration();
        });
        List<String> generalPublicPermissionStrings = generalPublicPermissions.stream().map(Enum::name).collect(Collectors.toCollection(LinkedList::new));
        List<String> generalMemberPermissionStrings = generalMemberPermissions.stream().map(Enum::name).collect(Collectors.toCollection(LinkedList::new));
        internalConfiguration.set("general-permissions.public", generalPublicPermissionStrings);
        internalConfiguration.set("general-permissions.member", generalMemberPermissionStrings);
        // claims is a section so no need to take care of that
        try {
            internalConfiguration.save(internalFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static YamlConfiguration initializeNation(String name, UUID leader) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("name", name);
        configuration.set("icon", Material.FENCE.name());
        configuration.set("leader", leader.toString());
        MemorySection.createPath(configuration, "members");
        MemorySection.createPath(configuration, "claims");
        return configuration;
    }

    public static String claimKey(World world, int chunkX) {
        return world.getUID().toString() + "." + chunkX;
    }
}
