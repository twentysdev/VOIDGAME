package me.krokodil69u.voidgame.commands

import me.krokodil69u.voidgame.types.EventType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class StartEventCMD : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        val gameWorld = Bukkit.getWorld("voidgame")!!
        try {
            EventType.valueOf(strings[0]).run()
        } catch (e: Exception) {
            commandSender.sendMessage("${ChatColor.RED}$e")
        }

        return true
    }
}