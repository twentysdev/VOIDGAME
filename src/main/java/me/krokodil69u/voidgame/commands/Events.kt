package me.krokodil69u.voidgame.commands

import me.krokodil69u.voidgame.VOIDGAME.Companion.instance
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class Events : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity.player

        instance!!.players.remove(player)
        player?.gameMode = GameMode.SPECTATOR
        player?.teleport(Location(player.world, 0.0, 110.0, 0.0))

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
}
