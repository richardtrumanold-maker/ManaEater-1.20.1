package com.example.manaeater;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class StartingGear {
    private StartingGear() {
    }

    public static void give(ServerPlayer player, PlayerClass clazz, ManaEaterData data) {
        if (clazz == null || data.isStartingGiven()) {
            return;
        }

        ItemStack weapon = getStartingWeapon(clazz);

        if (!weapon.isEmpty()) {
            player.addItem(weapon);
        }

        data.setStartingGiven(true);
    }

    public static ItemStack getStartingWeapon(PlayerClass clazz) {
        return switch (clazz) {
            case HUMAN -> new ItemStack(Items.IRON_SWORD);
            case GIANT -> new ItemStack(Items.IRON_AXE);
            case DWARF -> new ItemStack(Items.IRON_AXE);
            case VAMPIRE -> new ItemStack(Items.IRON_SWORD);
            case WEREWOLF -> new ItemStack(Items.IRON_AXE);

            case DAMPIR -> new ItemStack(Items.IRON_SWORD);
            case HALF_GIANT -> new ItemStack(Items.IRON_AXE);
            case HALF_DWARF -> new ItemStack(Items.IRON_PICKAXE);

            case VAMPIRE_GIANT -> new ItemStack(Items.IRON_SWORD);
            case WEREWOLF_GIANT -> new ItemStack(Items.IRON_AXE);

            case VAMPIRE_DWARF -> new ItemStack(Items.IRON_SWORD);
            case WEREWOLF_DWARF -> new ItemStack(Items.IRON_AXE);
        };
    }
}
