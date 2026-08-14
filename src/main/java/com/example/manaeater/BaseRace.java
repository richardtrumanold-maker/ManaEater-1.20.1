package com.example.manaeater;

import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.EnumSet;

public enum BaseRace {
    HUMAN("human", 0),
    UNCLEAN("unclean", 1),
    GIANT("giant", 2),
    DWARF("dwarf", 3);

    private final String id;
    private final int index;

    BaseRace(String id, int index) {
        this.id = id;
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public int getBit() {
        return 1 << index;
    }

    public Component getName() {
        return Component.translatable("base." + ManaEaterMod.MOD_ID + "." + id);
    }

    public static BaseRace byIndex(int index) {
        for (BaseRace race : values()) {
            if (race.index == index) {
                return race;
            }
        }
        return null;
    }

    public static BaseRace byId(String id) {
        for (BaseRace race : values()) {
            if (race.id.equals(id)) {
                return race;
            }
        }
        return null;
    }

    public static EnumSet<BaseRace> fromMask(int mask) {
        EnumSet<BaseRace> races = EnumSet.noneOf(BaseRace.class);

        for (BaseRace race : values()) {
            if ((mask & race.getBit()) != 0) {
                races.add(race);
            }
        }

        return races;
    }

    public static int toMask(Collection<BaseRace> races) {
        int mask = 0;

        for (BaseRace race : races) {
            mask |= race.getBit();
        }

        return mask;
    }
}
