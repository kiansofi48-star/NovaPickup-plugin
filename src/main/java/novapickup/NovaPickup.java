package me.novapixel.novapickup;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class NovaPickup extends JavaPlugin {

    private PickupManager pickupManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        pickupManager = new PickupManager(this);
        pickupManager.load();

        getServer().getPluginManager().registerEvents(
                new BlockPickupListener(pickupManager),
                this
        );

        PluginCommand command = getCommand("novapickup");

        if (command != null) {
            NovaPickupCommand executor =
                    new NovaPickupCommand(this, pickupManager);

            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        getLogger().info("NovaPickup enabled!");
    }

    public void reloadNovaPickup() {
        reloadConfig();
        pickupManager.load();
    }

    public PickupManager getPickupManager() {
        return pickupManager;
    }
}