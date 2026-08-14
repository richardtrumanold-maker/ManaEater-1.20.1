package com.example.manaeater;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ChooseSecondPayload(int baseIndex, int uncleanIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChooseSecondPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ManaEaterMod.MOD_ID, "choose_second")
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ChooseSecondPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    ChooseSecondPayload::baseIndex,
                    ByteBufCodecs.VAR_INT,
                    ChooseSecondPayload::uncleanIndex,
                    ChooseSecondPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ChooseSecondPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                return;
            }

            SelectionHandler.handleSecondary(
                    serverPlayer,
                    payload.baseIndex(),
                    payload.uncleanIndex()
            );
        });
    }
}
