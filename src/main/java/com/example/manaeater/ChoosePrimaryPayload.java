package com.example.manaeater;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ChoosePrimaryPayload(int baseIndex, int uncleanIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChoosePrimaryPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ManaEaterMod.MOD_ID, "choose_primary")
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ChoosePrimaryPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    ChoosePrimaryPayload::baseIndex,
                    ByteBufCodecs.VAR_INT,
                    ChoosePrimaryPayload::uncleanIndex,
                    ChoosePrimaryPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ChoosePrimaryPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                return;
            }

            SelectionHandler.handlePrimary(
                    serverPlayer,
                    payload.baseIndex(),
                    payload.uncleanIndex()
            );
        });
    }
}
