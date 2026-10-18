package me.novapixel.novapickup;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public final class PickupManager {

    private final NovaPickup plugin;

    private final Map<Material, PickupRule> rules =
            new EnumMap<>(Material.class);

    private final Set<String> worlds =
            new HashSet<>();

    public PickupManager(NovaPickup plugin) {
        this.plugin = plugin;
    }

    public void load() {

        rules.clear();
        worlds.clear();

        for (String world : plugin.getConfig().getStringList("worlds")) {

            if (world != null && !world.isBlank()) {
                worlds.add(
                        world.toLowerCase(Locale.ROOT)
                );
            }
        }

        ConfigurationSection section =
                plugin.getConfig()
                        .getConfigurationSection("blocks");

        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {

            Material block =
                    parseMaterial(key);

            Material drop =
                    parseMaterial(
                            section.getString(
                                    key + ".drop"
                            )
                    );

            int amount =
                    section.getInt(
                            key + ".amount",
                            1
                    );

            if (block == null) {
                plugin.getLogger().warning(
                        "Invalid block: " + key
                );
                continue;
            }

            if (drop == null) {
                plugin.getLogger().warning(
                        "Invalid drop for: " + key
                );
                continue;
            }

            if (amount <= 0) {
                plugin.getLogger().warning(
                        "Invalid amount for: " + key
                );
                continue;
            }

            rules.put(
                    block,
                    new PickupRule(
                            block,
                            drop,
                            amount
                    )
            );
        }

        plugin.getLogger().info(
                "Loaded " + rules.size()
                        + " NovaPickup rules."
        );
    }

    public boolean isWorldEnabled(World world) {

        return worlds.contains(
                world.getName()
                        .toLowerCase(Locale.ROOT)
        );
    }

    public PickupRule getRule(Material block) {
        return rules.get(block);
    }

    public boolean addRule(
            Material block,
            Material drop,
            int amount
    ) {

        if (rules.containsKey(block)) {
            return false;
        }

        String path =
                "blocks." + block.name();

        plugin.getConfig().set(
                path + ".drop",
                drop.name()
        );

        plugin.getConfig().set(
                path + ".amount",
                amount
        );

        plugin.saveConfig();

        load();

        return true;
    }

    public boolean deleteRule(Material block) {

        if (!rules.containsKey(block)) {
            return false;
        }

        plugin.getConfig().set(
                "blocks." + block.name(),
                null
        );

        plugin.saveConfig();

        load();

        return true;
    }

    public boolean editRule(
            Material block,
            Material drop,
            int amount
    ) {

        if (!rules.containsKey(block)) {
            return false;
        }

        String path =
                "blocks." + block.name();

        plugin.getConfig().set(
                path + ".drop",
                drop.name()
        );

        plugin.getConfig().set(
                path + ".amount",
                amount
        );

        plugin.saveConfig();

        load();

        return true;
    }

    private Material parseMaterial(String value) {

        if (value == null) {
            return null;
        }

        try {

            return Material.valueOf(
                    value.trim()
                            .toUpperCase(Locale.ROOT)
            );

        } catch (IllegalArgumentException e) {

            return null;
        }
    }
}