package com.example.manaeater;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class CardSelectScreen extends Screen {
    public record InfoLine(String text, int color) {
    }

    public record Card(String title, List<InfoLine> lines, int pickIndex) {
    }

    public static final int GREEN = 0xFF55FF55;
    public static final int RED = 0xFFFF5555;
    public static final int GRAY = 0xFFAAAAAA;
    public static final int YELLOW = 0xFFFFFF55;

    private static final int CARD_W = 80;
    private static final int CARD_H = 110;
    private static final int BTN_H = 18;
    private static final int GAP = 10;

    private final int kind;
    private final List<Card> cards;

    public CardSelectScreen(int kind, List<Card> cards) {
        super(Component.literal(""));
        this.kind = kind;
        this.cards = cards;
    }

    // ===== Открытие с клиента по данным пакета =====
    public static void open(int kind, int a, int b, int c, int d) {
        List<Card> cards = switch (kind) {
            case 0 -> baseRaceCards();
            case 1 -> uncleanCards();
            case 2 -> secondRaceCards(List.of(a, b, c, d));
            case 3 -> classCards(List.of(a, b, c, d));
            default -> List.of();
        };

        if (!cards.isEmpty()) {
            Minecraft.getInstance().setScreen(new CardSelectScreen(kind, cards));
        }
    }

    // ===== Сборки карточек =====
    public static List<Card> baseRaceCards() {
        List<Card> list = new ArrayList<>();
        for (BaseRace race : BaseRace.values()) {
            list.add(new Card(race.getName().getString(), raceLines(race), race.getIndex()));
        }
        return list;
    }

    public static List<Card> uncleanCards() {
        List<Card> list = new ArrayList<>();
        for (UncleanType type : UncleanType.values()) {
            list.add(new Card(type.getName().getString(), typeLines(type), type.getIndex()));
        }
        return list;
    }

    public static List<Card> secondRaceCards(List<Integer> indices) {
        List<Card> list = new ArrayList<>();
        for (int index : indices) {
            BaseRace race = BaseRace.byIndex(index);
            if (race != null) {
                list.add(new Card(race.getName().getString(), raceLines(race), race.getIndex()));
            }
        }
        return list;
    }

    public static List<Card> classCards(List<Integer> indices) {
        List<Card> list = new ArrayList<>();
        for (int index : indices) {
            PlayerClass clazz = PlayerClass.byIndex(index);
            if (clazz != null) {
                list.add(new Card(clazz.getName().getString(), classLines(clazz), clazz.getIndex()));
            }
        }
        return list;
    }

    // ===== Тексты (без упоминания классов и совместимостей) =====
    private static List<InfoLine> raceLines(BaseRace race) {
        return switch (race) {
            case HUMAN -> List.of(
                    new InfoLine("+2 здоровья", GREEN),
                    new InfoLine("+скорость", GREEN),
                    new InfoLine("универсал", GRAY)
            );
            case UNCLEAN -> List.of(
                    new InfoLine("ночная сила", YELLOW),
                    new InfoLine("выбор класса", YELLOW),
                    new InfoLine("слабость днём", RED)
            );
            case GIANT -> List.of(
                    new InfoLine("+6 здоровья", GREEN),
                    new InfoLine("+2 урона", GREEN),
                    new InfoLine("-скорость", RED)
            );
            case DWARF -> List.of(
                    new InfoLine("+2 прочности", GREEN),
                    new InfoLine("+скорость", GREEN),
                    new InfoLine("-рост", RED)
            );
        };
    }

    private static List<InfoLine> typeLines(UncleanType type) {
        return switch (type) {
            case VAMPIRE -> List.of(
                    new InfoLine("+скорость", GREEN),
                    new InfoLine("-2 здоровья", RED)
            );
            case WEREWOLF -> List.of(
                    new InfoLine("+2 урона", GREEN),
                    new InfoLine("-1 прочности", RED)
            );
        };
    }

    private static List<InfoLine> classLines(PlayerClass clazz) {
        return switch (clazz) {
            case HUMAN -> List.of(
                    new InfoLine("+2 здоровья", GREEN),
                    new InfoLine("+скорость", GREEN)
            );
            case GIANT -> List.of(
                    new InfoLine("+6 здоровья", GREEN),
                    new InfoLine("-скорость", RED)
            );
            case DWARF -> List.of(
                    new InfoLine("+2 прочности", GREEN),
                    new InfoLine("+скорость", GREEN)
            );
            case VAMPIRE -> List.of(
                    new InfoLine("+скорость", GREEN),
                    new InfoLine("-2 здоровья", RED)
            );
            case WEREWOLF -> List.of(
                    new InfoLine("+2 урона", GREEN),
                    new InfoLine("-1 прочности", RED)
            );
            case DAMPIR -> List.of(
                    new InfoLine("+1.5 урона", GREEN),
                    new InfoLine("+скорость", GREEN)
            );
            case HALF_GIANT -> List.of(
                    new InfoLine("+4 здоровья", GREEN),
                    new InfoLine("-скорость", RED)
            );
            case HALF_DWARF -> List.of(
                    new InfoLine("+2 прочности", GREEN),
                    new InfoLine("+1 здоровья", GREEN)
            );
            case VAMPIRE_GIANT -> List.of(
                    new InfoLine("+4 здоровья", GREEN),
                    new InfoLine("+скорость", GREEN)
            );
            case WEREWOLF_GIANT -> List.of(
                    new InfoLine("+5 здоровья", GREEN),
                    new InfoLine("+2 урона", GREEN)
            );
            case VAMPIRE_DWARF -> List.of(
                    new InfoLine("+1.5 прочности", GREEN),
                    new InfoLine("+скорость", GREEN)
            );
            case WEREWOLF_DWARF -> List.of(
                    new InfoLine("+1.5 урона", GREEN),
                    new InfoLine("+1 прочности", GREEN)
            );
        };
    }

    // ===== Рендер =====
    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        this.renderBackground(g, mx, my, pt);

        int total = cards.size() * CARD_W + (cards.size() - 1) * GAP;
        int x0 = (this.width - total) / 2;
        int y0 = (this.height - CARD_H - BTN_H) / 2;

        for (int i = 0; i < cards.size(); i++) {
            int x = x0 + i * (CARD_W + GAP);
            Card card = cards.get(i);

            // серая панель
            g.fill(x, y0, x + CARD_W, y0 + CARD_H, 0xFF3A3A3A);

            // заголовок
            g.drawCenteredString(this.font, card.title(), x + CARD_W / 2, y0 + 4, YELLOW);

            // строки баффов/дебаффов
            int ly = y0 + 16;
            for (InfoLine line : card.lines()) {
                g.drawCenteredString(this.font, line.text(), x + CARD_W / 2, ly
