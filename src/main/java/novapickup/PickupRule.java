package me.novapixel.novapickup;

import org.bukkit.Material;

public record PickupRule(
        Material block,
        Material drop,
        int amount
) {
}