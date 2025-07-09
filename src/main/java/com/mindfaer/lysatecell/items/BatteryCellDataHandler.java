package com.mindfaer.lysatecell.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public record BatteryCellDataHandler (byte cellin, int cellcharge, int celltype, int cellsize) {


    public static final Codec<BatteryCellDataHandler> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BYTE.fieldOf("cell_in").forGetter(BatteryCellDataHandler::cellin),
                    Codec.INT.fieldOf("cell_charge").forGetter(BatteryCellDataHandler::cellcharge),
                    Codec.INT.fieldOf("cell_type").forGetter(BatteryCellDataHandler::celltype),
                    Codec.INT.fieldOf("cell_size").forGetter(BatteryCellDataHandler::cellsize)

            ).apply(instance, BatteryCellDataHandler::new)
    );


    public static final StreamCodec<ByteBuf, BatteryCellDataHandler> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, BatteryCellDataHandler::cellin,
            ByteBufCodecs.INT, BatteryCellDataHandler::cellcharge,
            ByteBufCodecs.INT, BatteryCellDataHandler::celltype,
            ByteBufCodecs.INT, BatteryCellDataHandler::cellsize,

            BatteryCellDataHandler::new
    );

    //Make it work with other mods. Play nice y'all

    @Override
    public int hashCode() {
        return this.cellsize() * 11 + cellcharge() * 19 + cellin() * 3  +celltype() * 17;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatteryCellDataHandler that = (BatteryCellDataHandler) o;
        return cellin == that.cellin && cellcharge == that.cellcharge && celltype == that.celltype && cellsize == that.cellsize;
    }

}
