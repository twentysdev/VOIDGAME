package me.krokodil69u.voidgame.types

import me.krokodil69u.voidgame.VOIDGAME
import me.krokodil69u.voidgame.utils.Utils
import org.bukkit.Material
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import kotlin.random.Random

enum class EventType {
    ANVIL_RAIN {
        override fun run() {
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
    },
    EJECTION {
        override fun run() {
            for (player in VOIDGAME.instance!!.players) {
                player.velocity = Vector(player.velocity.x, player.velocity.y+2, player.velocity.z)
                player.addPotionEffect(
                        PotionEffect(
                                PotionEffectType.SLOW_FALLING,
                        100,
                        3
                        )
                )
            }
        }
    },
    RANDOM_EFFECT {
        override fun run() {
            for (player in VOIDGAME.instance!!.players) {
                player.addPotionEffect(Utils.instance!!
                        .getRandomPotionEffect(100..200, 1..5))
            }
        }
    };

    abstract fun run()
    val gameWorld = VOIDGAME.instance!!.gameWorld
}