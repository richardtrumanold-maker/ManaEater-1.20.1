package com.example.manaeater;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Collection;
import java.util.EnumSet;

public class ManaEaterData {
    private boolean finalized;
    private boolean pendingSecond;
    private boolean startingGiven;

    private EnumSet<BaseRace> baseRaces;
    private UncleanType uncleanType;
    private PlayerClass playerClass;

    public ManaEaterData() {
        this(false, false, false, 0, "", "");
    }

    private ManaEaterData(
            boolean finalized,
            boolean pendingSecond,
            boolean startingGiven,
            int mask,
            String uncleanId,
            String classId
    ) {
        this.finalized = finalized;
        this.pendingSecond = pendingSecond;
        this.startingGiven = startingGiven;
        this.baseRaces = BaseRace.fromMask(mask);
        this.uncleanType = uncleanId.isEmpty() ? null : UncleanType.byId(uncleanId);
        this.playerClass = classId.isEmpty() ? null : PlayerClass.byId(classId);
    }

    public static final Codec<ManaEaterData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("finalized", false).forGetter(data -> data.finalized),
            Codec.BOOL.optionalFieldOf("pending_second", false).forGetter(data -> data.pendingSecond),
            Codec.BOOL.optionalFieldOf("starting_given", false).forGetter(data -> data.startingGiven),
            Codec.INT.optionalFieldOf("mask", 0).forGetter(data -> BaseRace.toMask(data.baseRaces)),
            Codec.STRING.optionalFieldOf("unclean", "").forGetter(data -> data.uncleanType == null ? "" : data.uncleanType.getId()),
            Codec.STRING.optionalFieldOf("class", "").forGetter(data -> data.playerClass == null ? "" : data.playerClass.getId())
    ).apply(instance, ManaEaterData::new));

    public boolean isFinalized() {
        return finalized;
    }

    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }

    public boolean isPendingSecond() {
        return pendingSecond;
    }

    public void setPendingSecond(boolean pendingSecond) {
        this.pendingSecond = pendingSecond;
    }

    public boolean isStartingGiven() {
        return startingGiven;
    }

    public void setStartingGiven(boolean startingGiven) {
        this.startingGiven = startingGiven;
    }

    public EnumSet<BaseRace> getBaseRaces() {
        return baseRaces;
    }

    public void setBaseRaces(Collection<BaseRace> races) {
        this.baseRaces = races.isEmpty() ? EnumSet.noneOf(BaseRace.class) : EnumSet.copyOf(races);
    }

    public void addBaseRace(BaseRace race) {
        this.baseRaces.add(race);
    }

    public boolean hasBaseRace(BaseRace race) {
        return baseRaces.contains(race);
    }

    public UncleanType getUncleanType() {
        return uncleanType;
    }

    public void setUncleanType(UncleanType uncleanType) {
        this.uncleanType = uncleanType;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }
}
