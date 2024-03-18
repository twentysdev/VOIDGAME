package me.krokodil69u.voidgame.commands

import me.krokodil69u.voidgame.VOIDGAME.Companion.instance
import me.krokodil69u.voidgame.types.EventType
import me.krokodil69u.voidgame.types.SuperItemType
import me.krokodil69u.voidgame.utils.Utils
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random
import kotlin.random.nextInt


class StartGameCMD : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (instance!!.playing)
            return true

        instance!!.players = ArrayList(Bukkit.getOnlinePlayers())

        for (player in instance!!.players) {
            instance!!.oldPlayerLocations[player] = player.location
        }

        instance!!.playing = true

        clearGameField()
        placeGameField()

        var lavaLVL = 80.0

        instance!!.gameLoop = object : BukkitRunnable() {
            override fun run() {
                lavaLVL = updateBorders(lavaLVL)
                Utils.instance!!.giveRandomItemsToPlayers()
                if (Random.nextInt(1, 100) < 5) {
                    val eventType = EventType.entries.random()
                    Bukkit.broadcastMessage(ChatColor.YELLOW.toString() + "EVENT! -> " + ChatColor.GOLD.toString() + eventType.toString())
                    eventType.run()
                }
            }
        }.runTaskTimer(instance!!, 60, 160) // Повторять каждые 20 тиков (1 секунда)

        return true
    }

    private fun updateBorders(lastLavaLVL: Double): Double {
        val gameWorld = instance!!.gameWorld

        gameWorld.worldBorder.size -= 0.8

        val lavaRange = ceil(-gameWorld.worldBorder.size/2).toInt()..ceil(gameWorld.worldBorder.size/2).toInt()
        for (x in lavaRange) {
            for (z in lavaRange) {
                val replaceBlock = gameWorld.getBlockAt(x, floor(lastLavaLVL).toInt(),z)
                if (replaceBlock.type == Material.AIR && replaceBlock.type != Material.LAVA)
                    replaceBlock.type = Material.LAVA
            }
        }

        return lastLavaLVL+0.5f
    }

    private fun clearGameField() {
        val gameWorld = instance!!.gameWorld
        for (y in 256 downTo -65 + 1) {
            for (x in -30..30) {
                for (z in -30..30) {
                    gameWorld.getBlockAt(Location(gameWorld, x.toDouble(), y.toDouble(), z.toDouble())).type =
                        Material.AIR
                }
            }
        }
        for (e in gameWorld.entities) {
            if (e !is Player) e.remove()
        }

    }

    private fun placeGameField() {
        val gameWorld = instance!!.gameWorld
        val centerBlock = gameWorld.getBlockAt(Location(gameWorld, 0.0, 95.0, 0.0))
        centerBlock.type = Material.BEDROCK

        val chest = gameWorld.getBlockAt(Location(gameWorld, 0.0, (centerBlock.y+1).toDouble(), 0.0))
        chest.type = Material.CHEST
        val centerChest = chest.state as Chest

        for (i in 11..15) {
            centerChest.inventory.setItem(i, Utils.instance!!.getRandomItem())
        }

        val spawnPoints: MutableList<Block> = ArrayList()

        spawnPoints.add(gameWorld.getBlockAt(Location(gameWorld, 7.5, 100.0, 0.0)))
        spawnPoints.add(gameWorld.getBlockAt(Location(gameWorld, -7.5, 100.0, 0.0)))
        spawnPoints.add(gameWorld.getBlockAt(Location(gameWorld, 0.0, 100.0, 7.5)))
        spawnPoints.add(gameWorld.getBlockAt(Location(gameWorld, 0.0, 100.0, -7.5)))

        for (b in spawnPoints) {
            b.type = Material.BEDROCK
        }

        gameWorld.worldBorder.size = 30.0

        teleportPlayers(spawnPoints)
        gameWorld.worldBorder.warningDistance = 0
    }

    private fun teleportPlayers(spawnPoints: List<Block>) {
        var ind = 0
        for (p in instance!!.players) {
            p.gameMode = GameMode.SURVIVAL
            val sp = spawnPoints[ind].location
            sp.y += 1
            p.teleport(sp)
            p.isVisualFire = false
            p.inventory.clear()
            p.foodLevel = 20
            p.health = 20.0
            ind++
        }
    }
}
