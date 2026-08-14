package com.example.manaeater;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public final class ComboRules {
    public static final double SECOND_RACE_CHANCE = 0.08D;

    private ComboRules() {
    }

    public static List<BaseRace> getAllowedSecondRaces(
            EnumSet<BaseRace> currentRaces,
            UncleanType uncleanType
    ) {
        if (currentRaces == null || currentRaces.size() != 1) {
            return List.of();
        }

        BaseRace primary = currentRaces.iterator().next();
        List<BaseRace> allowed = new ArrayList<>();

        for (BaseRace candidate : BaseRace.values()) {
            if (candidate == primary) {
                continue;
            }

            if (canCombine(primary, candidate, uncleanType)) {
                allowed.add(candidate);
            }
        }

        return allowed;
    }

    public static boolean canCombine(BaseRace first, BaseRace second, UncleanType uncleanType) {
        if (first == null || second == null) {
            return false;
        }

        if (first == second) {
            return false;
        }

        // Гигант и Дворф остаются противоположностями.
        if ((first == BaseRace.GIANT && second == BaseRace.DWARF)
                || (first == BaseRace.DWARF && second == BaseRace.GIANT)) {
            return false;
        }

        if (first == BaseRace.UNCLEAN || second == BaseRace.UNCLEAN) {
            BaseRace other = first == BaseRace.UNCLEAN ? second : first;

            // Если тип Нечисти ещё не выбран, разрешаем сам выбор Нечисти,
            // а вампир/оборотень уже проверяются отдельно.
            if (uncleanType == null) {
                return true;
            }

            // Вампир совместим со всеми.
            if (uncleanType == UncleanType.VAMPIRE) {
                return true;
            }

            // Оборотень совместим со всеми, КРОМЕ человека.
            if (uncleanType == UncleanType.WEREWOLF) {
                return other != BaseRace.HUMAN;
            }
        }

        return true;
    }

    public static EnumSet<UncleanType> getAllowedUncleanTypes(BaseRace otherRace) {
        // Если выбираем чистую Нечисть без второй расы — доступны оба класса.
        if (otherRace == null) {
            return EnumSet.allOf(UncleanType.class);
        }

        // Если вторая раса/пара — Человек, то доступен только Вампир.
        if (otherRace == BaseRace.HUMAN) {
            return EnumSet.of(UncleanType.VAMPIRE);
        }

        // Для Гиганта и Дворфа доступны оба варианта:
        // Вампир и Оборотень.
        return EnumSet.allOf(UncleanType.class);
    }
}
