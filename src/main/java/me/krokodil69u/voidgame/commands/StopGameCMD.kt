package me.krokodil69u.voidgame.commands

import me.krokodil69u.voidgame.VOIDGAME.Companion.instance
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class StopGameCMD : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (!instance!!.playing) return true


        instance!!.gameLoop!!.cancel()

        val defaultLocation = Location(
                Bukkit.getWorld("overworld"),
                0.0, 0.0, 0.0
        )
        for (player in instance!!.gameWorld.players) {
            player.teleport(instance!!.oldPlayerLocations.getOrDefault(player, defaultLocation))
        }

        instance!!.players = arrayListOf()
        instance!!.playing = false

        Bukkit.broadcastMessage(ChatColor.RED.toString() + "Game is stopped by " + commandSender.name)

        return true
    }
}
