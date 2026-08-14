package com.example.manaeater;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PickCardPayload(int kind, int index) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PickCardPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ManaEaterMod.MOD_ID, "pick_card")
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, PickCardPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, PickCardPayload::kind,
                    ByteBufCodecs.VAR_INT, PickCardPayload::index,
                    PickCardPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PickCardPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                return;
            }

            ServerEvents.handlePick(serverPlayer, payload.kind(), payload.index());
        });
    }
}
