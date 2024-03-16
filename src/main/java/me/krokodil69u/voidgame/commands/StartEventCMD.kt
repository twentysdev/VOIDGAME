package me.krokodil69u.voidgame.commands

import me.krokodil69u.voidgame.VOIDGAME
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

class StartEventCMD : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        val gameWorld = Bukkit.getWorld("voidgame")!!
        for (i in EventType.entries) {
            if (i.toString() == strings[0]) {
                if (i == EventType.ANVIL_RAIN) {
                    val range = -gameWorld.worldBorder.size / 2..gameWorld.worldBorder.size/2
                    for (i in 0..10) {
                        gameWorld.getBlockAt(
                            Random.nextInt(range.start.toInt(), range.endInclusive.toInt()),
                            130,
                            Random.nextInt(range.start.toInt(), range.endInclusive.toInt())
                        ).type = Material.ANVIL
                    }
                } else if (i == EventType.BLINDNESS) {
                    for (j in VOIDGAME.instance!!.players) {
                        j.addPotionEffect(
                            PotionEffect(
                                PotionEffectType.BLINDNESS,
                                200,
                                5
                            )
                        )
                    }
                } else if (i == EventType.NAUSEA) {
                    for (j in VOIDGAME.instance!!.players) {
                        j.addPotionEffect(
                            PotionEffect(
                                PotionEffectType.CONFUSION,
                                200,
                                10
                            )
                        )
                    }
                }
            }
        }

        return true
    }
}