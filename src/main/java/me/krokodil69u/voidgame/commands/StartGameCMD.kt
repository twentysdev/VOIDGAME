package me.krokodil69u.voidgame.commands

import me.krokodil69u.voidgame.VOIDGAME.Companion.instance
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.TranslatableComponent
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
import kotlin.collections.ArrayList
import kotlin.math.floor

class StartGameCMD : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (instance!!.playing) return true

        for (p in Bukkit.getOnlinePlayers()){
            instance!!.players.add(p)
        }
        instance!!.playing = true

        val sender = commandSender as Player

        clearGameField(sender)
        placeGameField(sender)

        var lavaLVL = 86.0f

        instance!!.gameLoop = object : BukkitRunnable() {
            override fun run() {

                sender.world.worldBorder.size -= 0.9f

                for (x in -16..16) {
                    for (z in -16..16) {
                        val replaceBlock = sender.world.getBlockAt(x, floor(lavaLVL).toInt(),z)
                        if (replaceBlock.type == Material.AIR)
                            replaceBlock.type = Material.LAVA
                    }
                }

                lavaLVL += 0.5f

                for (p in instance!!.players) {
                    val x = randomItem()
                    p.inventory.addItem(x)
                    p.sendMessage(
                        ChatColor.YELLOW.toString() + "Items spawned! " +
                                ChatColor.GOLD.toString() + x.type.name
                    )
                    if (x.enchantments.size > 0)
                        for (dd in x.enchantments)
                            p.sendMessage(dd.key.toString() + " " + dd.value.toString())

                }
            }
        }.runTaskTimer(instance!!, 60, 160) // Повторять каждые 20 тиков (1 секунда)

        return true
    }

    private fun clearGameField(sender: Player) {
        for (y in 256 downTo -65 + 1) {
            for (x in -30..30) {
                for (z in -30..30) {
                    sender.world.getBlockAt(Location(sender.world, x.toDouble(), y.toDouble(), z.toDouble())).type =
                        Material.AIR
                }
            }
        }
        for (e in sender.world.entities) {
            if (e !is Player) e.remove()
        }
    }

    private fun placeGameField(sender: Player) {
        val centerBlock = sender.world.getBlockAt(Location(sender.world, 0.0, 100.0, 0.0))
        centerBlock.type = Material.BEDROCK

        val chest = sender.world.getBlockAt(Location(sender.world, 0.0, 101.0, 0.0))
        chest.type = Material.CHEST
        val centerChest = chest.state as Chest

        for (i in 11..15) {
            centerChest.inventory.setItem(i, randomItem())
        }

        val spawnPoints: MutableList<Block> = ArrayList()

        spawnPoints.add(sender.world.getBlockAt(Location(sender.world, 11.0, 100.0, 0.0)))
        spawnPoints.add(sender.world.getBlockAt(Location(sender.world, -11.0, 100.0, 0.0)))
        spawnPoints.add(sender.world.getBlockAt(Location(sender.world, 0.0, 100.0, 11.0)))
        spawnPoints.add(sender.world.getBlockAt(Location(sender.world, 0.0, 100.0, -11.0)))

        for (b in spawnPoints) {
            b.type = Material.BEDROCK
        }

        sender.world.worldBorder.size = 30.0

        teleportPlayers(spawnPoints)
    }

    private fun teleportPlayers(spawnPoints: List<Block>) {
        var ind = 0
        for (p in instance!!.players!!) {
            p.gameMode = GameMode.SURVIVAL
            val sp = spawnPoints[ind].location
            sp.y += 1
            p.teleport(sp)
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
                "copper_door", "wall_fun", "exposed"
            )

            var randomItem = ItemStack(Material.entries.toTypedArray().random())
            var x = bannedItems.contains(randomItem.type)

            for (i in bannedItemNames) {
                if (randomItem.type.name.lowercase().contains(i))
                    x = true
            }

            while (x) {
                randomItem = ItemStack(Material.entries.toTypedArray().random())

                x = bannedItems.contains(randomItem.type)
                for (i in bannedItemNames) {
                    if (randomItem.type.name.lowercase().contains(i)) x = false
                }
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
