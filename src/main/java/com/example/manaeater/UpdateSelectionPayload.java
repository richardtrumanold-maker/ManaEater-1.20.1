package com.example.manaeater;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.DistExecutor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateSelectionPayload(int baseMask, int uncleanIndex, int classIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateSelectionPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ManaEaterMod.MOD_ID, "update_selection")
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSelectionPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    UpdateSelectionPayload::baseMask,
                    ByteBufCodecs.VAR_INT,
                    UpdateSelectionPayload::uncleanIndex,
                    ByteBufCodecs.VAR_INT,
                    UpdateSelectionPayload::classIndex,
                    UpdateSelectionPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateSelectionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> DistExecutor.safeRunWhenOn(
                Dist.CLIENT,
                () -> () -> ClientNetworkHandler.updateSelection(
                        payload.baseMask(),
                        payload.uncleanIndex(),
                        payload.classIndex()
                )
        ));
    }
}
