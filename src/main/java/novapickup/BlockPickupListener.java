package me.novapixel.novapickup;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class BlockPickupListener implements Listener {

    private final PickupManager manager;

    public BlockPickupListener(PickupManager manager) {
        this.manager = manager;
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onBlockBreak(BlockBreakEvent event) {

        Player player =
                event.getPlayer();

        // فقط Worldهای تعریف‌شده
        if (!manager.isWorldEnabled(
                event.getBlock().getWorld()
        )) {
            return;
        }

        // قانون این بلاک
        PickupRule rule =
                manager.getRule(
                        event.getBlock().getType()
                );

        if (rule == null) {
            return;
        }

        ItemStack reward =
                new ItemStack(
                        rule.drop(),
                        rule.amount()
                );

        /*
         * قبل از شکستن بلاک بررسی می‌کنیم
         * که Inventory ظرفیت جایزه را دارد.
         *
         * این کار جلوی گم شدن آیتم را می‌گیرد.
         */
        if (!canFit(
                player.getInventory(),
                reward
        )) {

            event.setCancelled(true);

            player.sendMessage(
                    "§cInventory is full!"
            );

            return;
        }

        /*
         * جلوگیری از Drop معمولی بلاک
         */
        event.setDropItems(false);

        /*
         * دادن دقیقاً مقدار تنظیم‌شده
         */
        player.getInventory()
                .addItem(reward);
    }

    private boolean canFit(
            PlayerInventory inventory,
            ItemStack item
    ) {

        int remaining =
                item.getAmount();

        int max =
                item.getMaxStackSize();

        for (
                ItemStack current :
                inventory.getStorageContents()
        ) {

            // خانه خالی
            if (
                    current == null ||
                            current.getType() == Material.AIR
            ) {

                remaining -= max;

            }

            // استک مشابه
            else if (
                    current.isSimilar(item)
            ) {

                int free =
                        current.getMaxStackSize()
                                - current.getAmount();

                remaining -= free;
            }

            if (remaining <= 0) {
                return true;
            }
        }

        return remaining <= 0;
    }
}