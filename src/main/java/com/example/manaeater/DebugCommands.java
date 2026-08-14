package com.example.manaeater;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.EnumSet;

public final class DebugCommands {
    private DebugCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("manaeater")
                        .then(Commands.literal("open")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    PacketDistributor.sendToPlayer(player, new OpenRaceSelectPayload());
                                    return 1;
                                })
                        )
                        .then(Commands.literal("reset")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ManaEaterData data = player.getData(ManaEaterMod.MANA_DATA.get());
                                    
                                    data.setFinalized(false);
                                    data.setPendingSecond(false);
                                    data.setStartingGiven(false);
                                    data.setBaseRaces(EnumSet.noneOf(BaseRace.class));
                                    data.setUncleanType(null);
                                    data.setPlayerClass(null);
                                    
                                    ClassEffects.apply(player, null);
                                    
                                    context.getSource().sendSuccess(
                                            () -> Component.literal("Данные ManaEater сброшены. Перезайдите или используйте /manaeater open"), 
                                            false
                                    );
                                    return 1;
                                })
                        )
                        .then(Commands.literal("info")
                                .executes(DebugCommands::info)
                        )
                        .then(Commands.literal("forceclass")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("class", StringArgumentType.word())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                Arrays.stream(PlayerClass.values()).map(PlayerClass::getId).toList(),
                                                builder
                                        ))
                                        .executes(DebugCommands::forceClass)
                                )
                        )
        );
    }

    private static int info(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ManaEaterData data = player.getData(ManaEaterMod.MANA_DATA.get());

        String baseRaces = data.getBaseRaces().stream()
                .map(BaseRace::getId)
                .reduce((a, b) -> a + ", " + b)
                .orElse("none");
                
        String unclean = data.getUncleanType() == null ? "none" : data.getUncleanType().getId();
        String clazz = data.getPlayerClass() == null ? "none" : data.getPlayerClass().getId();

        context.getSource().sendSuccess(() -> Component.literal(
                "Finalized: " + data.isFinalized() +
                "\nPending Second: " + data.isPendingSecond() +
                "\nBase Races: [" + baseRaces + "]" +
                "\nUnclean Type: " + unclean +
                "\nClass: " + clazz
        ), false);
        
        return 1;
    }

    private static int forceClass(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String classId = StringArgumentType.getString(context, "class");
        PlayerClass clazz = PlayerClass.byId(classId);

        if (clazz == null) {
            context.getSource().sendFailure(Component.literal("Неизвестный класс: " + classId));
            return 0;
        }

        ManaEaterData data = player.getData(ManaEaterMod.MANA_DATA.get());
        data.setPlayerClass(clazz);
        data.setFinalized(true);
        data.setPendingSecond(false);

        ClassEffects.apply(player, clazz);

        context.getSource().sendSuccess(
                () -> Component.literal("Класс принудительно установлен: " + clazz.getId()), 
                false
        );
        
        return 1;
    }
}
