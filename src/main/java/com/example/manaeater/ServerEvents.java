package com.example.manaeater;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public final class ServerEvents {
    private ServerEvents() {
    }

    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ManaEaterData data = serverPlayer.getData(ManaEaterMod.MANA_DATA.get());

        if (data.isFinalized()) {
            SelectionHandler.sendSelectionUpdate(serverPlayer, data);
            ClassEffects.apply(serverPlayer, data.getPlayerClass());
            return;
        }

        if (data.getBaseRaces().isEmpty()) {
            PacketDistributor.sendToPlayer(serverPlayer, new OpenRaceSelectPayload());
            return;
        }

        if (data.isPendingSecond()) {
            List<BaseRace> allowed = ComboRules.getAllowedSecondRaces(
                    data.getBaseRaces(),
                    data.getUncleanType()
            );

            if (!allowed.isEmpty()) {
                SelectionHandler.sendSecondSelection(serverPlayer, allowed);
                return;
            }

            data.setPendingSecond(false);
        }

        SelectionHandler.finalizeSelection(serverPlayer);
    }

    public static void onPlayerClone(PlayerEvent.Clone event) {
        ManaEaterData oldData = event.getOriginal().getData(ManaEaterMod.MANA_DATA.get());
        ManaEaterData newData = event.getEntity().getData(ManaEaterMod.MANA_DATA.get());

        newData.setFinalized(oldData.isFinalized());
        newData.setPendingSecond(oldData.isPendingSecond());
        newData.setStartingGiven(oldData.isStartingGiven());
        newData.setBaseRaces(oldData.getBaseRaces());
        newData.setUncleanType(oldData.getUncleanType());
        newData.setPlayerClass(oldData.getPlayerClass());
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        // Здесь можно добавить команды для отладки, если нужно.
        // Например: /manaeater info, /manaeater open и т.д.
    }
}
