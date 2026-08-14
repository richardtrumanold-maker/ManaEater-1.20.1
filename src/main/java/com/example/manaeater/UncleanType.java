package com.example.manaeater;

import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.EnumSet;

public enum UncleanType {
    VAMPIRE("vampire", 0),
    WEREWOLF("werewolf", 1);

    private final String id;
    private final int index;

    UncleanType(String id, int index) {
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
        return Component.translatable("unclean." + ManaEaterMod.MOD_ID + "." + id);
    }

    public static UncleanType byIndex(int index) {
        for (UncleanType type : values()) {
            if (type.index == index) {
                return type;
            }
        }

        return null;
    }

    public static UncleanType byId(String id) {
        for (UncleanType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }

        return null;
    }

    public static EnumSet<UncleanType> fromMask(int mask) {
        EnumSet<UncleanType> types = EnumSet.noneOf(UncleanType.class);

        for (UncleanType type : values()) {
            if ((mask & type.getBit()) != 0) {
                types.add(type);
            }
        }

        return types;
    }

    public static int toMask(Collection<UncleanType> types) {
        int mask = 0;

        for (UncleanType type : types) {
            mask |= type.getBit();
        }

        return mask;
    }
}
