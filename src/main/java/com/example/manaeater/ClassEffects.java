package com.example.manaeater;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class ClassEffects {
    private ClassEffects() {
    }

    public static void apply(ServerPlayer player, PlayerClass clazz) {
        if (clazz == null) {
            return;
        }

        clearAllKnown(player);

        switch (clazz) {
            case HUMAN -> {
                add(player, Attributes.MAX_HEALTH, id(clazz, "health"), 2.0D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.MOVEMENT_SPEED, id(clazz, "speed"), 0.01D, AttributeModifier.Operation.ADD_VALUE);
            }

            case GIANT -> {
                add(player, Attributes.MAX_HEALTH, id(clazz, "health"), 6.0D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.ATTACK_DAMAGE, id(clazz, "damage"), 2.0D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.MOVEMENT_SPEED, id(clazz, "speed"), -0.03D, AttributeModifier.Operation.ADD_VALUE);
            }

            case DWARF -> {
                add(player, Attributes.ARMOR_TOUGHNESS, id(clazz, "toughness"), 2.0D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.MOVEMENT_SPEED, id(clazz, "speed"), 0.01D, AttributeModifier.Operation.ADD_VALUE);
            }

            case VAMPIRE -> {
                add(player, Attributes.MOVEMENT_SPEED, id(clazz, "speed"), 0.02D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.MAX_HEALTH, id(clazz, "health"), -2.0D, AttributeModifier.Operation.ADD_VALUE);
            }

            case WEREWOLF -> {
                add(player, Attributes.ATTACK_DAMAGE, id(clazz, "damage"), 2.0D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.ARMOR_TOUGHNESS, id(clazz, "toughness"), -1.0D, AttributeModifier.Operation.ADD_VALUE);
            }

            case DAMPIR -> {
                add(player, Attributes.ATTACK_DAMAGE, id(clazz, "damage"), 1.5D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.MOVEMENT_SPEED, id(clazz, "speed"), 0.01D, AttributeModifier.Operation.ADD_VALUE);
            }

            case HALF_GIANT -> {
                add(player, Attributes.MAX_HEALTH, id(clazz, "health"), 4.0D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.ATTACK_DAMAGE, id(clazz, "damage"), 1.0D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.MOVEMENT_SPEED, id(clazz, "speed"), -0.02D, AttributeModifier.Operation.ADD_VALUE);
            }

            case HALF_DWARF -> {
                add(player, Attributes.ARMOR_TOUGHNESS, id(clazz, "toughness"), 2.0D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.MAX_HEALTH, id(clazz, "health"), 1.0D, AttributeModifier.Operation.ADD_VALUE);
            }

            case VAMPIRE_GIANT -> {
                add(player, Attributes.MAX_HEALTH, id(clazz, "health"), 4.0D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.MOVEMENT_SPEED, id(clazz, "speed"), 0.01D, AttributeModifier.Operation.ADD_VALUE);
            }

            case WEREWOLF_GIANT -> {
                add(player, Attributes.MAX_HEALTH, id(clazz, "health"), 5.0D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.ATTACK_DAMAGE, id(clazz, "damage"), 2.0D, AttributeModifier.Operation.ADD_VALUE);
            }

            case VAMPIRE_DWARF -> {
                add(player, Attributes.ARMOR_TOUGHNESS, id(clazz, "toughness"), 1.5D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.MOVEMENT_SPEED, id(clazz, "speed"), 0.02D, AttributeModifier.Operation.ADD_VALUE);
            }

            case WEREWOLF_DWARF -> {
                add(player, Attributes.ATTACK_DAMAGE, id(clazz, "damage"), 1.5D, AttributeModifier.Operation.ADD_VALUE);
                add(player, Attributes.ARMOR_TOUGHNESS, id(clazz, "toughness"), 1.0D, AttributeModifier.Operation.ADD_VALUE);
            }
        }
    }

    private static void clearAllKnown(ServerPlayer player) {
        for (PlayerClass clazz : PlayerClass.values()) {
            remove(player, Attributes.MAX_HEALTH, id(clazz, "health"));
            remove(player, Attributes.MOVEMENT_SPEED, id(clazz, "speed"));
            remove(player, Attributes.ATTACK_DAMAGE, id(clazz, "damage"));
            remove(player, Attributes.ARMOR_TOUGHNESS, id(clazz, "toughness"));
        }
    }

    private static void add(
            ServerPlayer player,
            Holder<Attribute> attribute,
            ResourceLocation id,
            double amount,
            AttributeModifier.Operation operation
    ) {
        AttributeInstance instance = player.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        instance.removeModifier(id);
        instance.addPermanentModifier(new AttributeModifier(id, amount, operation));
    }

    private static void remove(ServerPlayer player, Holder<Attribute> attribute, ResourceLocation id) {
        AttributeInstance instance = player.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        instance.removeModifier(id);
    }

    private static ResourceLocation id(PlayerClass clazz, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(
                ManaEaterMod.MOD_ID,
                clazz.getId() + "_" + suffix
        );
    }
}
