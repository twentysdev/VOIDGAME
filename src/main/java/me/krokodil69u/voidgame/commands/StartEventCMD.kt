package me.krokodil69u.voidgame.commands

import me.krokodil69u.voidgame.VOIDGAME
import me.krokodil69u.voidgame.other.EventType
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

class StartEventCMD : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        val gameWorld = Bukkit.getWorld("voidgame")!!
        try {
            EventType.valueOf(strings[0]).run(gameWorld)
        } catch (e: Exception) {
            commandSender.sendMessage("${ChatColor.RED}$e")
        }

        return true
    }
}