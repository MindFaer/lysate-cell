package com.mindfaer.lysatecell.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record BatteryCellDataHandler(boolean cellin, int celltype, int cellsize, int cellcharge) {

    public static final Codec < BatteryCellDataHandler > CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("cell_in").forGetter(BatteryCellDataHandler::cellin),
                    Codec.INT.fieldOf("cell_type").forGetter(BatteryCellDataHandler::celltype),
                    Codec.INT.fieldOf("cell_size").forGetter(BatteryCellDataHandler::cellsize),
                    Codec.INT.fieldOf("cell_charge").forGetter(BatteryCellDataHandler::cellcharge)

            ).apply(instance, BatteryCellDataHandler::new)
    );

    public static final StreamCodec < ByteBuf, BatteryCellDataHandler > STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, BatteryCellDataHandler::cellin,
            ByteBufCodecs.INT, BatteryCellDataHandler::celltype,
            ByteBufCodecs.INT, BatteryCellDataHandler::cellsize,
            ByteBufCodecs.INT, BatteryCellDataHandler::cellcharge,

            BatteryCellDataHandler::new
    );

    //Make it work with other mods. Play nice y'all

    @Override
    public int hashCode() {
        return (cellin() ? 1 : 0) * 11 + celltype * 3 + cellsize * 17 + cellcharge * 7;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatteryCellDataHandler that = (BatteryCellDataHandler) o;
        return cellin == that.cellin && celltype == that.celltype && cellsize == that.cellsize && cellcharge == that.cellcharge;
    }

}