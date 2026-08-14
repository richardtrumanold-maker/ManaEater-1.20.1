package com.example.manaeater;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public final class ServerEvents {
    // "Обычные" классы для рандома 4 из N.
    // Если хочешь ровно 6 — добавь сюда шестой класс.
    private static final List<PlayerClass> NORMAL_POOL = List.of(
            PlayerClass.HUMAN,
            PlayerClass.GIANT,
            PlayerClass.DWARF,
            PlayerClass.VAMPIRE,
            PlayerClass.WEREWOLF
    );

    private ServerEvents() {
    }

    // ===== Вход игрока =====
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ManaEaterData data = serverPlayer.getData(ManaEaterMod.MANA_DATA.get());

        if (data.isFinalized()) {
            SelectionHandler.sendSelectionUpdate(serverPlayer, data);
            ClassEffects.apply(serverPlayer, data.getPlayerClass());
            return;
        }

        if (data.getBaseRaces().isEmpty()) {
            openBase(serverPlayer);
            return;
        }

        // Если раса уже есть, но тип Нечисти не выбран — докачиваем выбор.
        if (data.getBaseRaces().contains(BaseRace.UNCLEAN) && data.getUncleanType() == null) {
            openUnclean(serverPlayer);
            return;
        }

        openClassWindow(serverPlayer);
    }

    public static void onPlayerClone(PlayerEvent.Clone event) {
        ManaEaterData oldData = event.getOriginal().getData(ManaEaterMod.MANA_DATA.get());
        ManaEaterData newData = event.getEntity().getData(ManaEaterMod.MANA_DATA.get());

        newData.setFinalized(oldData.isFinalized());
        newData.setPendingSecond(oldData.isPendingSecond());
        newData.setStartingGiven(oldData.isStartingGiven());
        newData.setBaseRaces(oldData.getBaseRaces());
        newData.setUncleanType(oldData.getUncleanType());
        newData.setPlayerClass(oldData.getPlayerClass());
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        DebugCommands.register(event.getDispatcher());
    }

    // ===== Обработка выбора карточки =====
    public static void handlePick(ServerPlayer player, int kind, int index) {
        switch (kind) {
            case 0 -> pickBase(player, index);
            case 1 -> pickUnclean(player, index);
            case 2 -> pickSecond(player, index);
            case 3 -> pickClass(player, index);
            default -> {
            }
        }
    }

    private static void pickBase(ServerPlayer player, int index) {
        ManaEaterData data = player.getData(ManaEaterMod.MANA_DATA.get());

        if (data.isFinalized() || !data.getBaseRaces().isEmpty()) {
            return;
        }

        BaseRace base = BaseRace.byIndex(index);

        if (base == null) {
            return;
        }

        data.setBaseRaces(EnumSet.of(base));

        if (base == BaseRace.UNCLEAN) {
            openUnclean(player);
        } else {
            rollSecondOrClass(player);
        }
    }

    private static void pickUnclean(ServerPlayer player, int index) {
        ManaEaterData data = player.getData(ManaEaterMod.MANA_DATA.get());

        UncleanType type = UncleanType.byIndex(index);

        if (type == null || !data.getBaseRaces().contains(BaseRace.UNCLEAN)) {
            return;
        }

        data.setUncleanType(type);

        // Если Нечисть была второй расой — сразу окно класса.
        if (data.getBaseRaces().size() == 2) {
            openClassWindow(player);
        } else {
            rollSecondOrClass(player);
        }
    }

    private static void pickSecond(ServerPlayer player, int index) {
        ManaEaterData data = player.getData(ManaEaterMod.MANA_DATA.get());

        if (data.getBaseRaces().size() != 1) {
            return;
        }

        BaseRace second = BaseRace.byIndex(index);

        if (second == null || data.hasBaseRace(second)) {
            return;
        }

        BaseRace primary = data.getBaseRaces().iterator().next();

        if (!ComboRules.canCombine(primary, second, data.getUncleanType())) {
            player.displayClientMessage(
                    Component.translatable("manaeater.message.forbidden_combo"),
                    true
            );
            return;
        }

        data.addBaseRace(second);

        if (second == BaseRace.UNCLEAN) {
            openUnclean(player);
        } else {
            openClassWindow(player);
        }
    }

    private static void pickClass(ServerPlayer player, int index) {
        ManaEaterData data = player.getData(ManaEaterMod.MANA_DATA.get());

        if (data.isFinalized()) {
            return;
        }

        PlayerClass clazz = PlayerClass.byIndex(index);

        if (clazz == null) {
            return;
        }

        // Если выпал специальный класс с Нечистью, а тип ещё не выбран — ставим его.
        if (data.getUncleanType() == null && data.getBaseRaces().contains(BaseRace.UNCLEAN)) {
            UncleanType fromSpecial = uncleanTypeOf(clazz);
            if (fromSpecial != null) {
                data.setUncleanType(fromSpecial);
            }
        }

        // Если при двух расах выбран обычный класс — вторую расу сбрасываем.
        if (data.getBaseRaces().size() == 2 && uncleanTypeOf(clazz) == null
                && clazz != PlayerClass.determine(data.getBaseRaces(), data.getUncleanType())) {
            BaseRace primary = data.getBaseRaces().iterator().next();
            data.setBaseRaces(EnumSet.of(primary));
        }

        data.setPlayerClass(clazz);
        data.setFinalized(true);
        data.setPendingSecond(false);

        SelectionHandler.sendSelectionUpdate(player, data);
        ClassEffects.apply(player, clazz);
        StartingGear.give(player, clazz, data);

        player.displayClientMessage(
                Component.literal("Ваш класс: " + clazz.getId()),
                false
        );
    }

    // ===== Логика окон =====
    private static void rollSecondOrClass(ServerPlayer player) {
        ManaEaterData data = player.getData(ManaEaterMod.MANA_DATA.get());

        if (player.getRandom().nextDouble() < ComboRules.SECOND_RACE_CHANCE) {
            List<BaseRace> allowed = ComboRules.getAllowedSecondRaces(
                    data.getBaseRaces(),
                    data.getUncleanType()
            );

            if (!allowed.isEmpty()) {
                openSecond(player, allowed);
                return;
            }
        }

        openClassWindow(player);
    }

    private static void openBase(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new OpenCardsPayload(0, -1, -1, -1, -1));
    }

    private static void openUnclean(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new OpenCardsPayload(1, -1, -1, -1, -1));
    }

    private static void openSecond(ServerPlayer player, List<BaseRace> allowed) {
        int[] idx = new int[]{-1, -1, -1, -1};
        for (int i = 0; i < allowed.size() && i < 4; i++) {
            idx[i] = allowed.get(i).getIndex();
        }

        PacketDistributor.sendToPlayer(player, new OpenCardsPayload(2, idx[0], idx[1], idx[2], idx[3]));
    }

    private static void openClassWindow(ServerPlayer player) {
        ManaEaterData data = player.getData(ManaEaterMod.MANA_DATA.get());

        // Рандомим 4 из обычных.
        List<PlayerClass> pool = new ArrayList<>(NORMAL_POOL);
        Collections.shuffle(pool, new Random(player.getRandom().nextLong()));

        List<PlayerClass> options = new ArrayList<>(pool.subList(0, Math.min(4, pool.size())));

        // Если комбинация рас — 1 специальный вместо 1 обычного.
        if (data.getBaseRaces().size() == 2) {
            List<PlayerClass> specials = validSpecials(data);

            if (!specials.isEmpty() && !options.isEmpty()) {
                PlayerClass special = specials.get(new Random(player.getRandom().nextLong()).nextInt(specials.size()));
                options.set(options.size() - 1, special);
            }
        }

        int[] idx = new int[]{-1, -1, -1, -1};
        for (int i = 0; i < options.size() && i < 4; i++) {
            idx[i] = options.get(i).getIndex();
        }

        PacketDistributor.sendToPlayer(player, new OpenCardsPayload(3, idx[0], idx[1], idx[2], idx[3]));
    }

    private static List<PlayerClass> validSpecials(ManaEaterData data) {
        List<PlayerClass> specials = new ArrayList<>();

        if (!data.getBaseRaces().contains(BaseRace.UNCLEAN)) {
            PlayerClass single = PlayerClass.determine(data.getBaseRaces(), data.getUncleanType());
            if (single != null) {
                specials.add(single);
            }
            return specials;
        }

        if (data.getUncleanType() != null) {
            PlayerClass single = PlayerClass.determine(data.getBaseRaces(), data.getUncleanType());
            if (single != null) {
                specials.add(single);
            }
            return specials;
        }

        // Тип Нечисти ещё не выбран: 2 варианта (вампир/оборотень).
        for (UncleanType type : UncleanType.values()) {
            PlayerClass variant = PlayerClass.determine(data.getBaseRaces(), type);
            if (variant != null && !specials.contains(variant)) {
                specials.add(variant);
            }
        }

        return specials;
    }

    private static UncleanType uncleanTypeOf(PlayerClass clazz) {
        return switch (clazz) {
            case DAMPIR, VAMPIRE_GIANT, VAMPIRE_DWARF -> UncleanType.VAMPIRE;
            case WEREWOLF_GIANT, WEREWOLF_DWARF -> UncleanType.WEREWOLF;
            case VAMPIRE -> UncleanType.VAMPIRE;
            case WEREWOLF -> UncleanType.WEREWOLF;
            default -> null;
        };
    }
}
