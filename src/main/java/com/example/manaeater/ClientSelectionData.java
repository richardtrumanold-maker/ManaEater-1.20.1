package com.example.manaeater;

public final class ClientSelectionData {
    private static int baseMask = 0;
    private static int uncleanIndex = -1;
    private static int classIndex = -1;

    private ClientSelectionData() {
    }

    public static void update(int newBaseMask, int newUncleanIndex, int newClassIndex) {
        baseMask = newBaseMask;
        uncleanIndex = newUncleanIndex;
        classIndex = newClassIndex;
    }

    public static int getBaseMask() {
        return baseMask;
    }

    public static boolean hasBaseRace(BaseRace race) {
        return (baseMask & race.getBit()) != 0;
    }

    public static UncleanType getUncleanType() {
        return UncleanType.byIndex(uncleanIndex);
    }

    public static PlayerClass getPlayerClass() {
        return PlayerClass.byIndex(classIndex);
    }

    public static boolean hasClass() {
        return classIndex != -1;
    }
}
