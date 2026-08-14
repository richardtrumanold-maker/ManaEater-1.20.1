package com.example.manaeater;

import net.minecraft.client.Minecraft;

public class ClientNetworkHandler {
    public static void openRaceSelectScreen() {
        Minecraft.getInstance().setScreen(new BaseRaceSelectScreen());
    }

    public static void openSecondRaceSelectScreen(int baseMask, int uncleanMask) {
        Minecraft.getInstance().setScreen(new SecondRaceSelectScreen(baseMask, uncleanMask));
    }

    public static void updateSelection(int baseMask, int uncleanIndex, int classIndex) {
        ClientSelectionData.update(baseMask, uncleanIndex, classIndex);
    }
}
