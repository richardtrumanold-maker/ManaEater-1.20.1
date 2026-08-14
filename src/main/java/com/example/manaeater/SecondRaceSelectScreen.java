package com.example.manaeater;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class SecondRaceSelectScreen extends Screen {
    private final int baseMask;
    private final int uncleanMask;

    public SecondRaceSelectScreen(int baseMask, int uncleanMask) {
        super(Component.translatable("manaeater.screen.second_title"));
        this.baseMask = baseMask;
        this.uncleanMask = uncleanMask;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 60;

        int visibleIndex = 0;

        for (BaseRace race : BaseRace.values()) {
            if ((baseMask & race.getBit()) == 0) {
                continue;
            }

            int offsetY = visibleIndex * 24;

            this.addRenderableWidget(
                    Button.builder(race.getName(), button -> {
                                if (race == BaseRace.UNCLEAN) {
                                    Minecraft.getInstance().setScreen(
                                            new UncleanTypeSelectScreen(true, uncleanMask)
                                    );
                                } else {
                                    PacketDistributor.sendToServer(
                                            new ChooseSecondPayload(race.getIndex(), -1)
                                    );
                                    this.onClose();
                                }
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
