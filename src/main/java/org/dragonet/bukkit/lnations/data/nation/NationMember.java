package org.dragonet.bukkit.lnations.data.nation;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 2017/11/17.
 */
public class NationMember {

    private final Nation nation;
    private final ConfigurationSection internalNationMemberConfiguration;

    private final UUID memberUniqueId;

    private final Set<NationPermission> enabledPermissions;
    private boolean changed;

    public NationMember(Nation nation, ConfigurationSection internalNationMemberConfiguration, UUID memberUniqueId) {
        this.nation = nation;
        this.internalNationMemberConfiguration = internalNationMemberConfiguration;
        this.memberUniqueId = memberUniqueId;
        enabledPermissions = new HashSet<>();
        enabledPermissions.addAll(internalNationMemberConfiguration.getStringList("enabled-permissions").stream().map(NationPermission::valueOf).collect(Collectors.toList()));
    }

    public void setPermission(NationPermission permission, boolean option) {
        if(enabledPermissions.contains(permission) == option);
        nation.markChanged();
        markChanged();
        if(option) {
            enabledPermissions.add(permission);
        } else {
            enabledPermissions.remove(permission);
        }
    }

    public UUID getMemberUniqueId() {
        return memberUniqueId;
    }

    public boolean hasPermission(NationPermission permission) {
        return enabledPermissions.contains(permission);
    }

    public void markChanged(){
        changed = true;
    }

    public boolean isChanged() {
        return changed;
    }

    public void updateInternalConfiguration() {
        List<String> permissions = enabledPermissions.stream().map(Enum::name).collect(Collectors.toCollection(LinkedList::new));
        internalNationMemberConfiguration.set("enabled-permissions", permissions);
    }

    public static ConfigurationSection initializeNationMember(UUID player) {
        YamlConfiguration section = new YamlConfiguration();
        MemorySection.createPath(section, "enabled-permissions");
        return section;
    }
}
