package com.example.manaeater;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.EnumSet;
import java.util.List;

public final class SelectionHandler {
    private SelectionHandler() {
    }

    public static void handlePrimary(ServerPlayer player, int baseIndex, int uncleanIndex) {
        ManaEaterData data = player.getData(ManaEaterMod.MANA_DATA.get());

        if (data.isFinalized()) {
            return;
        }

        if (!data.getBaseRaces().isEmpty()) {
            return;
        }

        BaseRace base = BaseRace.byIndex(baseIndex);

        if (base == null) {
            return;
        }

        UncleanType type = null;

        if (base == BaseRace.UNCLEAN) {
            type = UncleanType.byIndex(uncleanIndex);

            if (type == null) {
                return;
            }
        } else {
            if (uncleanIndex != -1) {
                return;
            }
        }

        data.addBaseRace(base);
        data.setUncleanType(type);

        if (player.getRandom().nextDouble() < ComboRules.SECOND_RACE_CHANCE) {
            List<BaseRace> allowed = ComboRules.getAllowedSecondRaces(
                    data.getBaseRaces(),
                    data.getUncleanType()
            );

            if (!allowed.isEmpty()) {
                data.setPendingSecond(true);
                sendSecondSelection(player, allowed);
                return;
            }
        }

        finalizeSelection(player);
    }

    public static void handleSecondary(ServerPlayer player, int baseIndex, int uncleanIndex) {
        ManaEaterData data = player.getData(ManaEaterMod.MANA_DATA.get());

        if (data.isFinalized()) {
            return;
        }

        if (!data.isPendingSecond()) {
            return;
        }

        if (data.getBaseRaces().size() != 1) {
            return;
        }

        BaseRace second = BaseRace.byIndex(baseIndex);

        if (second == null) {
            return;
        }

        if (data.hasBaseRace(second)) {
            return;
        }

        BaseRace primary = data.getBaseRaces().iterator().next();
        UncleanType finalUncleanType = data.getUncleanType();

        if (second == BaseRace.UNCLEAN) {
            UncleanType secondType = UncleanType.byIndex(uncleanIndex);

            if (secondType == null) {
                return;
            }

            if (!ComboRules.getAllowedUncleanTypes(primary).contains(secondType)) {
                player.displayClientMessage(
                        Component.translatable("manaeater.message.forbidden_combo"),
                        true
                );
                return;
            }

            finalUncleanType = secondType;
        } else {
            if (uncleanIndex != -1) {
                return;
            }
        }

        if (!ComboRules.canCombine(primary, second, finalUncleanType)) {
            player.displayClientMessage(
                    Component.translatable("manaeater.message.forbidden_combo"),
                    true
            );
            return;
        }

        data.addBaseRace(second);

        if (second == BaseRace.UNCLEAN) {
            data.setUncleanType(finalUncleanType);
        }

        data.setPendingSecond(false);

        finalizeSelection(player);
    }

    public static void sendSecondSelection(ServerPlayer player, List<BaseRace> allowed) {
        ManaEaterData data = player.getData(ManaEaterMod.MANA_DATA.get());

        if (data.getBaseRaces().isEmpty()) {
            return;
        }

        BaseRace primary = data.getBaseRaces().iterator().next();

        int baseMask = BaseRace.toMask(allowed);

        int uncleanMask = 0;

        if (allowed.contains(BaseRace.UNCLEAN)) {
            uncleanMask = UncleanType.toMask(ComboRules.getAllowedUncleanTypes(primary));
        }

        PacketDistributor.sendToPlayer(player, new OpenSecondRaceSelectPayload(baseMask, uncleanMask));
    }

    public static void finalizeSelection(ServerPlayer player) {
        ManaEaterData data = player.getData(ManaEaterMod.MANA_DATA.get());

        if (data.getBaseRaces().isEmpty()) {
            return;
        }

        PlayerClass clazz = PlayerClass.determine(data.getBaseRaces(), data.getUncleanType());

        // Если комбинация по какой-то причине недопустима,
        // откатываемся к первой расе.
        if (clazz == null) {
            BaseRace primary = data.getBaseRaces().iterator().next();
            EnumSet<BaseRace> fallback = EnumSet.of(primary);

            clazz = PlayerClass.determine(fallback, data.getUncleanType());
            data.setBaseRaces(fallback);
        }

        data.setPlayerClass(clazz);
        data.setFinalized(true);
        data.setPendingSecond(false);

        sendSelectionUpdate(player, data);

        ClassEffects.apply(player, clazz);
        StartingGear.give(player, clazz, data);

        player.displayClientMessage(
                Component.literal("Ваш класс: " + (clazz == null ? "unknown" : clazz.getId())),
                false
        );
    }

    public static void sendSelectionUpdate(ServerPlayer player, ManaEaterData data) {
        int baseMask = BaseRace.toMask(data.getBaseRaces());
        int uncleanIndex = data.getUncleanType() == null ? -1 : data.getUncleanType().getIndex();
        int classIndex = data.getPlayerClass() == null ? -1 : data.getPlayerClass().getIndex();

        PacketDistributor.sendToPlayer(player, new UpdateSelectionPayload(baseMask, uncleanIndex, classIndex));
    }
}
