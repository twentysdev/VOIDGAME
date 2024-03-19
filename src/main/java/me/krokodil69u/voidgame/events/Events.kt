package me.krokodil69u.voidgame.events

import me.krokodil69u.voidgame.VOIDGAME.Companion.instance
import me.krokodil69u.voidgame.types.SuperItemType
import me.krokodil69u.voidgame.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.random.Random

class Events : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity.player

        if (!instance!!.players.contains(player) && !instance!!.playing)
            return

        for (item in player?.inventory?.contents!!) {
            if (item == null)
                continue
            if (Random.nextInt(1,4) == 2)
                player.killer?.inventory?.addItem(item)
        }

        instance!!.players.remove(player)
        player.gameMode = GameMode.SPECTATOR
        player.teleport(Location(Bukkit.getWorld("voidgame"), 0.0, 110.0, 0.0))

        if (instance!!.players.size <= 1) {
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
            Utils.instance!!.stopGame()
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (!instance!!.players.contains(player) && !instance!!.playing)
            return

        if (event.action != Action.RIGHT_CLICK_AIR &&
            event.action != Action.RIGHT_CLICK_BLOCK)
            return

        for (itemType in SuperItemType.entries) {
            if (event.item?.itemMeta?.displayName.equals(itemType.name_)) {
                Bukkit.broadcastMessage(event.item?.itemMeta?.displayName!!)
                itemType.use(player)
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        if (!instance!!.playing && !instance!!.players.contains(player))
            return

        instance!!.players.remove(player)

        if (instance!!.players.size == 0)
            Utils.instance!!.stopGame()
        else if (instance!!.players.size == 1) {
            // winner
        }
    }
}
