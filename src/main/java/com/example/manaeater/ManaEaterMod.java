package com.example.manaeater;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@Mod(ManaEaterMod.MOD_ID)
public class ManaEaterMod {
    public static final String MOD_ID = "mana_eater";

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

    public static final Supplier<AttachmentType<ManaEaterData>> MANA_DATA =
            ATTACHMENT_TYPES.register(
                    "mana_eater_data",
                    () -> AttachmentType.builder(ManaEaterData::new)
                            .serialize(ManaEaterData.CODEC)
                            .build()
            );

    public ManaEaterMod(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);

        modBus.addListener(ManaEaterMod::registerPayloads);

        NeoForge.EVENT_BUS.addListener(ServerEvents::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(ServerEvents::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(ServerEvents::onRegisterCommands);
    }

    private static void registerPayloads(final RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar("1");

    registrar.playToClient(
            OpenCardsPayload.TYPE,
            OpenCardsPayload.STREAM_CODEC,
            OpenCardsPayload::handle
    );

    registrar.playToServer(
            PickCardPayload.TYPE,
            PickCardPayload.STREAM_CODEC,
            PickCardPayload::handle
    );

    registrar.playToClient(
            UpdateSelectionPayload.TYPE,
            UpdateSelectionPayload.STREAM_CODEC,
            UpdateSelectionPayload::handle
    );
}
