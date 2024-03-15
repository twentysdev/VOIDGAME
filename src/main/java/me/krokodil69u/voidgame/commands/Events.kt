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
        if (!instance!!.playing || !instance!!.players.contains(player!!)) {
            return
        }

        instance!!.players.minus(player)
        player.gameMode = GameMode.SPECTATOR
        player.teleport(Location(player.world, 0.0, 110.0, 0.0))

        if (instance!!.players.size <= 1) {
            instance!!.playing = false
            instance!!.gameLoop!!.cancel()

            if (instance!!.players.size == 1) {
                val winner = instance!!.players[0]
                instance!!.players = arrayListOf()
                Bukkit.broadcastMessage(ChatColor.GOLD.toString() + winner.name + ChatColor.WHITE + " win!")
                for (p in Bukkit.getOnlinePlayers()) {
                    p.sendTitle(ChatColor.GOLD.toString() + winner.name, "win", 20, 60, 20)
                }
            }
        }
    }
}
