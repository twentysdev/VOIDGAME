package me.krokodil69u.voidgame.commands

import me.krokodil69u.voidgame.VOIDGAME.Companion.instance
import me.krokodil69u.voidgame.other.EventType
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
        instance!!.playing = true

        clearGameField()
        placeGameField()

        var lavaLVL = 80.0

        instance!!.gameLoop = object : BukkitRunnable() {
            override fun run() {
                lavaLVL = updateBorders(lavaLVL)
                giveItems()
                if (Random.nextInt(1, 100) < 5) {
                    val eventType = EventType.entries.random()
                    Bukkit.broadcastMessage(ChatColor.YELLOW.toString() + "EVENT! -> " + ChatColor.GOLD.toString() + eventType.toString())
                    eventType.run()
                }
            }
        }.runTaskTimer(instance!!, 60, 160) // Повторять каждые 20 тиков (1 секунда)

        return true
    }
    private fun giveItems() {
        for (p in instance!!.players) {
            val x = randomItem()
            p.inventory.addItem(x)
            p.sendMessage(
                    ChatColor.YELLOW.toString() + "Items spawned! " +
                            ChatColor.GOLD.toString() + x.type.name.replace('_', ' ')
            )
        }
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
            centerChest.inventory.setItem(i, randomItem())
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

    private fun randomItem(): ItemStack {
        val bannedItems = listOf(
            Material.AIR, Material.VOID_AIR, Material.OXIDIZED_COPPER_GRATE,
            Material.EXPOSED_COPPER_GRATE, Material.WAXED_OXIDIZED_COPPER_GRATE,
            Material.POLISHED_TUFF, Material.POLISHED_TUFF_SLAB, Material.POLISHED_TUFF_STAIRS,
            Material.POLISHED_TUFF_WALL, Material.WAXED_EXPOSED_COPPER_DOOR,
            Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK,
            Material.TUFF_BRICK_SLAB, Material.TUFF_BRICKS, Material.TUFF_BRICK_STAIRS, Material.TUFF_SLAB,
            Material.TUFF_BRICK_WALL, Material.CHISELED_TUFF_BRICKS, Material.CHISELED_TUFF,
            Material.WAXED_EXPOSED_COPPER_GRATE, Material.WITHER_SKELETON_WALL_SKULL,
            Material.CRAFTER, Material.COPPER_DOOR, Material.WAXED_COPPER_DOOR,
            Material.WAXED_COPPER_TRAPDOOR, Material.COPPER_TRAPDOOR, Material.END_GATEWAY,
            Material.NETHER_PORTAL, Material.END_PORTAL, Material.FROSTED_ICE,
            Material.LIGHT
        )
        val bannedItemNames = listOf(
            "air", "template", "pottery_sherd", "dye", "candle_cake", "command", "bulb",
            "wall_banner", "wall_sign", "potted", "knowledge", "trial", "jigsaw",
            "void", "grate", "redstone_wire", "hanging", "cauldron", "copper_trapdoor",
            "copper_door", "wall_fun", "exposed", "map", "chiseled_copper"
        )

        var randomItem = ItemStack(Material.entries.toTypedArray().random())
        var isAvailable = bannedItems.contains(randomItem.type)

        for (bannedItem in bannedItemNames) {
            if (randomItem.type.name.lowercase().contains(bannedItem))
                isAvailable = true
        }

        while (isAvailable) {
            randomItem = ItemStack(Material.entries.toTypedArray().random())

            isAvailable = bannedItems.contains(randomItem.type)
            for (bannedItemName in bannedItemNames) {
                if (randomItem.type.name.lowercase().contains(bannedItemName))
                    isAvailable = true
            }
        }

        val randomPercentage = Random.nextInt(0..100)
        if (randomPercentage <= 10) {
            val meta = randomItem.itemMeta
            meta?.setDisplayName("Random effect to random player / RMB")
            randomItem.setItemMeta(meta)
        } else if (randomPercentage <= 14) {
            val meta = randomItem.itemMeta
            meta?.setDisplayName("Switch with random player / RMB")
            randomItem.setItemMeta(meta)
        } else if (randomPercentage <= 15) {
            val meta = randomItem.itemMeta
            meta?.setDisplayName("Switch INVENTORY with random player / RMB")
            randomItem.setItemMeta(meta)
        }

        if ((Math.random() * 5).toInt() == 3) {
            val enc = Enchantment.values().random()
            var power = 1 + (Math.random() * 10).toInt()

            if (enc == Enchantment.DAMAGE_ALL && power > 5)
                power = 5

            randomItem.addUnsafeEnchantment(
                enc, power
            )
        }
        return randomItem
    }
}
