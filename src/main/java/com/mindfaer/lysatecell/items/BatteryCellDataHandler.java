package com.mindfaer.lysatecell.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
public record BatteryCellDataHandler (int cellin, int cellcharge, int celltype, int cellsize) {


    public static final Codec<BatteryCellDataHandler> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("cellin").forGetter(BatteryCellDataHandler::cellin),
                    Codec.INT.fieldOf("cellcharge").forGetter(BatteryCellDataHandler::cellcharge),
                    Codec.INT.fieldOf("celltype").forGetter(BatteryCellDataHandler::celltype),
                    Codec.INT.fieldOf("cellsize").forGetter(BatteryCellDataHandler::cellsize)

            ).apply(instance, BatteryCellDataHandler::new)
    );


    public static final StreamCodec<ByteBuf, BatteryCellDataHandler> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, BatteryCellDataHandler::cellin,
            ByteBufCodecs.INT, BatteryCellDataHandler::cellcharge,
            ByteBufCodecs.INT, BatteryCellDataHandler::celltype,
            ByteBufCodecs.INT, BatteryCellDataHandler::cellsize,

            BatteryCellDataHandler::new
    );

}
