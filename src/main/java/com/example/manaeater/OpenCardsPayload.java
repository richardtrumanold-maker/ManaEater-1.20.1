package com.example.manaeater;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.DistExecutor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenCardsPayload(int kind, int a, int b, int c, int d) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenCardsPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ManaEaterMod.MOD_ID, "open_cards")
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCardsPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, OpenCardsPayload::kind,
                    ByteBufCodecs.VAR_INT, OpenCardsPayload::a,
                    ByteBufCodecs.VAR_INT, OpenCardsPayload::b,
                    ByteBufCodecs.VAR_INT, OpenCardsPayload::c,
                    ByteBufCodecs.VAR_INT, OpenCardsPayload::d,
                    OpenCardsPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenCardsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> DistExecutor.safeRunWhenOn(
                Dist.CLIENT,
                () -> () -> CardSelectScreen.open(
                        payload.kind(), payload.a(), payload.b(), payload.c(), payload.d()
                )
        ));
    }
}
