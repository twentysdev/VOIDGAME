package me.krokodil69u.voidgame.commands

import me.krokodil69u.voidgame.VOIDGAME.Companion.instance
import me.krokodil69u.voidgame.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class StopGameCMD : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (!instance!!.playing) return true
        Utils.instance!!.stopGame()
        Bukkit.broadcastMessage(ChatColor.RED.toString() + "Game is stopped by " + commandSender.name)
        return true
    }
}
