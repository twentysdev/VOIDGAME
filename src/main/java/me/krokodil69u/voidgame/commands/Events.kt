package me.krokodil69u.voidgame.commands

import me.krokodil69u.voidgame.VOIDGAME.Companion.instance
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Effect
import org.bukkit.EntityEffect
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import kotlin.random.Random

class Events : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity.player

        if (!instance!!.players.contains(player) && !instance!!.playing)
            return

        instance!!.players.remove(player)
        player?.gameMode = GameMode.SPECTATOR
        player?.teleport(Location(Bukkit.getWorld("voidgame"), 0.0, 110.0, 0.0))

        if (instance!!.players.size <= 1) {
            instance!!.playing = false
            instance!!.gameLoop!!.cancel()

            if (instance!!.players.size == 1) {
                val winner = instance!!.players[0]
                if (instance!!.playerWins.contains(winner))
                    instance!!.playerWins[winner] = instance!!.playerWins[winner]!!+1
                else
                    instance!!.playerWins[winner] = 1
                instance!!.players = arrayListOf()
                for (ff in instance!!.playerWins) {
                    Bukkit.broadcastMessage(ChatColor.GREEN.toString() + ff.key.name + " - " + ff.value.toString())
                }
                for (p in Bukkit.getOnlinePlayers()) {
                    p.sendTitle(ChatColor.GOLD.toString() + winner.name, "win", 20, 60, 20)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (!instance!!.players.contains(player) && !instance!!.playing)
            return

        if (event.action == Action.RIGHT_CLICK_AIR ||
            event.action == Action.RIGHT_CLICK_BLOCK) {
            val rndPlayer = instance!!.players.random()

            if (event.item?.itemMeta?.displayName.equals("Random effect to random player / RMB")) {
                val rndPotion = PotionEffect(
                    PotionEffectType.values().random(),
                    Random.nextInt(100, 300),
                    Random.nextInt(1, 6)
                )
                rndPlayer.addPotionEffect(
                    rndPotion
                )
                Bukkit.broadcastMessage("${ChatColor.GOLD}${player.name} ${ChatColor.YELLOW}applied an ${ChatColor.GOLD}${rndPotion}" +
                        "${ChatColor.YELLOW} to ${ChatColor.GOLD}${rndPlayer.name}")
                player.inventory.remove(event.material)
            }
            if (event.item?.itemMeta?.displayName.equals("Switch with random player / RMB")) {
                val playerOldPos = player.location
                player.teleport(rndPlayer.location)
                rndPlayer.teleport(playerOldPos)
                player.inventory.remove(event.material)
                Bukkit.broadcastMessage("${ChatColor.GOLD}${player.name} ${ChatColor.YELLOW}switch with ${ChatColor.GOLD}${player.name}")
            }
        }
    }
}
