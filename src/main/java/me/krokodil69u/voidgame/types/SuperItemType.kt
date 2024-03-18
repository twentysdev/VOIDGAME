package me.krokodil69u.voidgame.types

import me.krokodil69u.voidgame.VOIDGAME
import me.krokodil69u.voidgame.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

enum class SuperItemType {
    /*

        SORT BY FROM MIN TO MAX CHANCE IN PERCENT

     */

    SWITCH_INVENTORY_WITH_RANDOM_PLAYER {
        override val chanceInPercent = 1.0f
        override val material = Material.SLIME_BALL
        override val name_ = "Switch INVENTORY with random player"
        override fun use(user: Player) {
            val randomPlayer = VOIDGAME.instance!!.players.random()
            user.inventory.remove(material)
            val playerOldInventory = user.inventory.contents
            user.inventory.contents = randomPlayer.inventory.contents
            randomPlayer.inventory.contents = playerOldInventory
            Bukkit.broadcastMessage("${ChatColor.GOLD}${user.name} ${ChatColor.YELLOW}switch INVENTORY with ${ChatColor.GOLD}${randomPlayer.name}")
        }
    },
    SWITCH_WITH_RANDOM_PLAYER {
        override val chanceInPercent = 3.0f
        override val material = Material.SLIME_BALL
        override val name_ = "Switch with random player"
        override fun use(user: Player) {
            val randomPlayer = VOIDGAME.instance!!.players.random()
            val playerOldPos = user.location
            user.teleport(randomPlayer.location)
            randomPlayer.teleport(playerOldPos)
            user.inventory.remove(material)
            Bukkit.broadcastMessage("${ChatColor.GOLD}${user.name} ${ChatColor.YELLOW}switch with ${ChatColor.GOLD}${randomPlayer.name}")
        }
    },
    RANDOM_EFFECT_TO_RANDOM_PLAYER {
        override val chanceInPercent = 8.0f
        override val material = Material.SLIME_BALL
        override val name_ = "Random effect to random player"
        override fun use(user: Player) {
            val onPlayer = VOIDGAME.instance!!.players.random()
            val rndPotion = Utils.instance!!.getRandomPotionEffect(100..300, 1..6)

            onPlayer.addPotionEffect(
                    rndPotion
            )

            Bukkit.broadcastMessage("${ChatColor.GOLD}${user.name} ${ChatColor.YELLOW}applied an ${ChatColor.GOLD}${rndPotion}" +
                    "${ChatColor.YELLOW} to ${ChatColor.GOLD}${onPlayer.name}")
            user.inventory.remove(material)
        }
    };
    abstract fun use(user: Player)
    abstract val material: Material
    abstract val chanceInPercent: Float
    abstract val name_: String
}