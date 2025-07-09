package com.mindfaer.lysatecell.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BatteryCellDataHandler (byte cellin, int cellcharge, int celltype, int cellsize) {


    public static final Codec<BatteryCellDataHandler> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BYTE.fieldOf("cellin").forGetter(BatteryCellDataHandler::cellin),
                    Codec.INT.fieldOf("cellcharge").forGetter(BatteryCellDataHandler::cellcharge),
                    Codec.INT.fieldOf("celltype").forGetter(BatteryCellDataHandler::celltype),
                    Codec.INT.fieldOf("cellsize").forGetter(BatteryCellDataHandler::cellsize)

            ).apply(instance, BatteryCellDataHandler::new)
    );


    public static final StreamCodec<ByteBuf, BatteryCellDataHandler> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, BatteryCellDataHandler::cellin,
            ByteBufCodecs.INT, BatteryCellDataHandler::cellcharge,
            ByteBufCodecs.INT, BatteryCellDataHandler::celltype,
            ByteBufCodecs.INT, BatteryCellDataHandler::cellsize,

            BatteryCellDataHandler::new
    );


    public BatteryCellDataHandler onItem(boolean heldByOwner) {
        return new BatteryCellDataHandler(cellin, cellcharge, celltype, cellsize);
    }

}
