package com.example.manaeater;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.DistExecutor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenRaceSelectPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenRaceSelectPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ManaEaterMod.MOD_ID, "open_race_select")
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenRaceSelectPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenRaceSelectPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenRaceSelectPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> DistExecutor.safeRunWhenOn(
                Dist.CLIENT,
                () -> () -> ClientNetworkHandler.openRaceSelectScreen()
        ));
    }
}
