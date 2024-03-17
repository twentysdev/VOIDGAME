package me.krokodil69u.voidgame.commands

import jdk.jfr.Event
import me.krokodil69u.voidgame.VOIDGAME.Companion.instance
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random
import kotlin.random.nextInt


class StartGameCMD : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (instance!!.playing) return true

        for (p in Bukkit.getOnlinePlayers()){
            instance!!.players.add(p)
        }
        instance!!.playing = true

        val wc = WorldCreator("VOIDGAME")
        wc.generator(EmptyChunkGenerator())
        val gameWorld = wc.createWorld()

        clearGameField(gameWorld!!)
        placeGameField(gameWorld)

        var lavaLVL = 80.0f

        instance!!.gameLoop = object : BukkitRunnable() {
            override fun run() {

                gameWorld.worldBorder.size -= 0.8f
                val lavaRange = ceil(-gameWorld.worldBorder.size/2).toInt()..ceil(gameWorld.worldBorder.size/2).toInt()

                for (x in lavaRange) {
                    for (z in lavaRange) {
                        val replaceBlock = gameWorld.getBlockAt(x, floor(lavaLVL).toInt(),z)
                        if (replaceBlock.type == Material.AIR && replaceBlock.type != Material.LAVA)
                            replaceBlock.type = Material.LAVA
                    }
                }

                lavaLVL += 0.5f

                for (p in instance!!.players) {
                    val x = randomItem()
                    p.inventory.addItem(x)
                    p.sendMessage(
                        ChatColor.YELLOW.toString() + "Items spawned! " +
                                ChatColor.GOLD.toString() + x.type.name.replace('_', ' ')
                    )
                }

                if (Random.nextInt(1, 100) < 5) {
                    val eventType = EventType.entries.random()
                    Bukkit.broadcastMessage(ChatColor.YELLOW.toString() + "EVENT! -> " + ChatColor.GOLD.toString() + eventType.toString())

                    if (eventType == EventType.ANVIL_RAIN) {
                        val range = -gameWorld.worldBorder.size / 2..gameWorld.worldBorder.size/2
                        for (i in 0..10) {
                            gameWorld.getBlockAt(
                                Random.nextInt(range.start.toInt(), range.endInclusive.toInt()),
                                130,
                                Random.nextInt(range.start.toInt(), range.endInclusive.toInt())
                            ).type = Material.ANVIL
                        }
                    } else if (eventType == EventType.BLINDNESS) {
                        for (i in instance!!.players) {
                            i.addPotionEffect(
                                PotionEffect(
                                    PotionEffectType.BLINDNESS,
                                    200,
                                    5
                                )
                            )
                        }
                    } else if (eventType == EventType.NAUSEA) {
                        for (i in instance!!.players) {
                            i.addPotionEffect(
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
        }.runTaskTimer(instance!!, 60, 160) // Повторять каждые 20 тиков (1 секунда)

        return true
    }

    private fun clearGameField(gameWorld: World) {
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

    private fun placeGameField(gameWorld: World) {
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
            var x = bannedItems.contains(randomItem.type)

            for (i in bannedItemNames) {
                if (randomItem.type.name.lowercase().contains(i))
                    x = true
            }

            while (x) {
                randomItem = ItemStack(Material.entries.toTypedArray().random())

                x = bannedItems.contains(randomItem.type)
                for (i in bannedItemNames) {
                    if (!randomItem.type.name.lowercase().contains(i)) x = false
                }
            }
            val z = (Math.random() * 10).toInt()
            val zx = (Math.random() * 50).toInt()
            val zxc = (Math.random() * 80).toInt()
            if (z == 5) {
                val meta = randomItem.itemMeta
                meta?.setDisplayName("Random effect to random player / RMB")
                randomItem.setItemMeta(meta)
            } else if (zx == 10) {
                val meta = randomItem.itemMeta
                meta?.setDisplayName("Switch with random player / RMB")
                randomItem.setItemMeta(meta)
            } else if (zxc == 15) {
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
