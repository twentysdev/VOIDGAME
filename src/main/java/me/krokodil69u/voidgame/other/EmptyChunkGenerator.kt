package me.krokodil69u.voidgame.other

import org.bukkit.World
import org.bukkit.generator.ChunkGenerator
import javax.annotation.Nonnull
import kotlin.random.Random


class EmptyChunkGenerator : ChunkGenerator() {
    @Nonnull
    fun generateChunkData(
        @Nonnull world: World?,
        @Nonnull random: Random?,
        x: Int,
        z: Int,
        @Nonnull biome: BiomeGrid?
    ): ChunkData {
        return createChunkData(world!!)
    }
}