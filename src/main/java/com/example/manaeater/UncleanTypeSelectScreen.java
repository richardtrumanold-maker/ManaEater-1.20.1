package com.example.manaeater;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class UncleanTypeSelectScreen extends Screen {
    private final boolean second;
    private final int allowedMask;

    public UncleanTypeSelectScreen(boolean second, int allowedMask) {
        super(Component.translatable("manaeater.screen.unclean_title"));
        this.second = second;
        this.allowedMask = allowedMask;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 30;

        int visibleIndex = 0;

        for (UncleanType type : UncleanType.values()) {
            if ((allowedMask & type.getBit()) == 0) {
                continue;
            }

            int offsetY = visibleIndex * 24;

            this.addRenderableWidget(
                    Button.builder(type.getName(), button -> {
                                if (!second) {
                                    PacketDistributor.sendToServer(
                                            new ChoosePrimaryPayload(BaseRace.UNCLEAN.getIndex(), type.getIndex())
                                    );
                                } else {
                                    PacketDistributor.sendToServer(
                                            new ChooseSecondPayload(BaseRace.UNCLEAN.getIndex(), type.getIndex())
                                    );
                                }

                                this.onClose();
                            })
                            .bounds(x, y + offsetY, 200, 20)
                            .build()
            );

            visibleIndex++;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(
                this.font,
                this.title,
                this.width / 2,
                20,
                0xFFFFFF
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
