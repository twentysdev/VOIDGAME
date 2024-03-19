package me.krokodil69u.voidgame.utils

import me.krokodil69u.voidgame.VOIDGAME
import me.krokodil69u.voidgame.types.SuperItemType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random
import kotlin.random.nextInt

class Utils {
    fun getRandomPotionEffect(durationRangeInTicks: IntRange,
                              amplifierRange: IntRange) : PotionEffect
    {
        var potionEffect: PotionEffect = _getRandomPotionEffect(
                durationRangeInTicks,
                amplifierRange
        )

        while (potionEffect.type != PotionEffectType.HARM &&
                potionEffect.type != PotionEffectType.WITHER) {
            potionEffect = _getRandomPotionEffect(
                    durationRangeInTicks,
                    amplifierRange
            )
        }
        return potionEffect
    }
    fun giveRandomItemsToPlayers() {
        for (player in VOIDGAME.instance!!.players) {
            val item = getRandomItem()
            player.inventory.addItem(item)
            player.sendMessage(
                    ChatColor.YELLOW.toString() + "Items spawned! " +
                            ChatColor.GOLD.toString() + item.type.name.replace('_', ' ')
            )
        }
    }

    fun stopGame() {
        VOIDGAME.instance!!.gameLoop!!.cancel()

        val defaultLocation = Location(
                Bukkit.getWorld("overworld"),
                0.0, 0.0, 0.0
        )
        for (player in VOIDGAME.instance!!.gameWorld.players) {
            player.teleport(VOIDGAME.instance!!.oldPlayerLocations.getOrDefault(player, defaultLocation))
        }

        VOIDGAME.instance!!.players = arrayListOf()
        VOIDGAME.instance!!.playing = false
    }

    fun getRandomItem(): ItemStack {
        var randomItem = ItemStack(Material.entries.random())

        while (!itemIsAvailable(randomItem.type)) {
            randomItem = ItemStack(Material.entries.random())
        }

        val randomPercentage = Random.nextInt(0..100)

        for (item in SuperItemType.entries) {
            if (randomPercentage <= item.chanceInPercent) {
                val meta = randomItem.itemMeta
                meta?.setDisplayName(item.name_)
                randomItem.setItemMeta(meta)
                randomItem.type = item.material
                break
            }
        }

        if (Random.nextInt(0..5) == 3) {
            val enchantment = Enchantment.values().random()
            var power = 1 + (Math.random() * 10).toInt()

            if (enchantment == Enchantment.DAMAGE_ALL && power > 5)
                power = 5

            randomItem.addUnsafeEnchantment(
                    enchantment, power
            )
        }
        return randomItem
    }
    private fun itemIsAvailable(item: Material): Boolean {
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
                Material.LIGHT, Material.KELP_PLANT
        )
        val bannedItemNames = listOf(
                "template", "pottery_sherd", "dye", "candle_cake", "command", "bulb",
                "wall_banner", "wall_sign", "potted", "knowledge", "trial", "jigsaw",
                "void", "grate", "redstone_wire", "hanging", "cauldron", "copper_trapdoor",
                "copper_door", "wall_fun", "exposed", "map", "chiseled_copper", "grate"
        )

        var isAvailable = !bannedItems.contains(item)
        for (bannedItem in bannedItemNames) {
            if (item.name.lowercase().contains(bannedItem)) {
                isAvailable = false
                break
            }
        }
        return isAvailable
    }
    private fun _getRandomPotionEffect(durationRangeInTicks: IntRange,
                                       amplifierRange: IntRange) : PotionEffect
    {
        return PotionEffect(
                PotionEffectType.values().random(),
                Random.nextInt(durationRangeInTicks),
                Random.nextInt(amplifierRange)
        )
    }
    companion object {
        @JvmStatic
        var instance: Utils? = Utils()
            private set
    }
}