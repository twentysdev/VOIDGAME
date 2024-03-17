package me.krokodil69u.voidgame.other

import me.krokodil69u.voidgame.VOIDGAME
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

enum class EventType {
    ANVIL_RAIN {
        override fun run() {
            val gameWorld = VOIDGAME.instance!!.gameWorld
            val range = -gameWorld.worldBorder.size / 2..gameWorld.worldBorder.size/2
            for (i in 0..10) {
                gameWorld.getBlockAt(
                        Random.nextInt(range.start.toInt(), range.endInclusive.toInt()),
                        130,
                        Random.nextInt(range.start.toInt(), range.endInclusive.toInt())
                ).type = Material.ANVIL
            }
       }
   },
    BLINDNESS {
    override fun run() {
        for (i in VOIDGAME.instance!!.players) {
            i.addPotionEffect(
                PotionEffect(
                    PotionEffectType.BLINDNESS,
                    200,
                    5
                    )
                )
            }
        }
    },
    NAUSEA {
        override fun run() {
            for (i in VOIDGAME.instance!!.players) {
                i.addPotionEffect(
                        PotionEffect(
                                PotionEffectType.CONFUSION,
                                200,
                                10
                        )
                )
            }
        }
    };

    abstract fun run()
}