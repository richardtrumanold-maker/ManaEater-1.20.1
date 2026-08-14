package com.example.manaeater;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.EnumSet;

public class BaseRaceSelectScreen extends Screen {
    public BaseRaceSelectScreen() {
        super(Component.translatable("manaeater.screen.base_title"));
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 60;

        for (BaseRace race : BaseRace.values()) {
            int offsetY = race.getIndex() * 24;

            this.addRenderableWidget(
                    Button.builder(race.getName(), button -> {
                                if (race == BaseRace.UNCLEAN) {
                                    Minecraft.getInstance().setScreen(
                                            new UncleanTypeSelectScreen(
                                                    false,
                                                    UncleanType.toMask(EnumSet.allOf(UncleanType.class))
                                            )
                                    );
                                } else {
                                    PacketDistributor.sendToServer(new ChoosePrimaryPayload(race.getIndex(), -1));
                                    this.onClose();
                                }
                            })
                            .bounds(x, y + offsetY, 200, 20)
                            .build()
            );
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
