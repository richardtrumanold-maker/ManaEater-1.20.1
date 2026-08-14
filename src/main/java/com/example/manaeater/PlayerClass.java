package com.example.manaeater;

import net.minecraft.network.chat.Component;

import java.util.EnumSet;

public enum PlayerClass {
    HUMAN("human", 0),
    GIANT("giant", 1),
    DWARF("dwarf", 2),
    VAMPIRE("vampire", 3),
    WEREWOLF("werewolf", 4),

    DAMPIR("dampir", 5),
    HALF_GIANT("half_giant", 6),
    HALF_DWARF("half_dwarf", 7),

    VAMPIRE_GIANT("vampire_giant", 8),
    WEREWOLF_GIANT("werewolf_giant", 9),

    VAMPIRE_DWARF("vampire_dwarf", 10),
    WEREWOLF_DWARF("werewolf_dwarf", 11);

    private final String id;
    private final int index;

    PlayerClass(String id, int index) {
        this.id = id;
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public Component getName() {
        return Component.translatable("class." + ManaEaterMod.MOD_ID + "." + id);
    }

    public static PlayerClass byIndex(int index) {
        for (PlayerClass clazz : values()) {
            if (clazz.index == index) {
                return clazz;
            }
        }

        return null;
    }

    public static PlayerClass byId(String id) {
        for (PlayerClass clazz : values()) {
            if (clazz.id.equals(id)) {
                return clazz;
            }
        }

        return null;
    }

    public static PlayerClass determine(EnumSet<BaseRace> races, UncleanType uncleanType) {
        if (races == null || races.isEmpty()) {
            return null;
        }

        if (races.size() == 1) {
            BaseRace race = races.iterator().next();

            return switch (race) {
                case HUMAN -> HUMAN;
                case GIANT -> GIANT;
                case DWARF -> DWARF;
                case UNCLEAN -> uncleanType == UncleanType.WEREWOLF ? WEREWOLF : VAMPIRE;
            };
        }

        if (races.size() == 2) {
            boolean human = races.contains(BaseRace.HUMAN);
            boolean giant = races.contains(BaseRace.GIANT);
            boolean dwarf = races.contains(BaseRace.DWARF);
            boolean unclean = races.contains(BaseRace.UNCLEAN);

            if (human && giant) {
                return HALF_GIANT;
            }

            if (human && dwarf) {
                return HALF_DWARF;
            }

            if (giant && dwarf) {
                return null;
            }

            if (human && unclean) {
                // Человек совместим только с Вампиром.
                // Человек + Оборотень запрещено.
                return uncleanType == UncleanType.WEREWOLF ? null : DAMPIR;
            }

            if (giant && unclean) {
                // Гигант совместим и с Вампиром, и с Оборотнем.
                if (uncleanType == UncleanType.VAMPIRE) {
                    return VAMPIRE_GIANT;
                }

                if (uncleanType == UncleanType.WEREWOLF) {
                    return WEREWOLF_GIANT;
                }

                return null;
            }

            if (dwarf && unclean) {
                // Дворф совместим и с Вампиром, и с Оборотнем.
                if (uncleanType == UncleanType.VAMPIRE) {
                    return VAMPIRE_DWARF;
                }

                if (uncleanType == UncleanType.WEREWOLF) {
                    return WEREWOLF_DWARF;
                }

                return null;
            }
        }

        return null;
    }
}
