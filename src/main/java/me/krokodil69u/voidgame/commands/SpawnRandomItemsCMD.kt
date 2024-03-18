package me.krokodil69u.voidgame.commands

import me.krokodil69u.voidgame.VOIDGAME
import me.krokodil69u.voidgame.utils.Utils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class SpawnRandomItemsCMD : CommandExecutor {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if (VOIDGAME.instance!!.playing)
            Utils.instance!!.giveRandomItemsToPlayers()
        return true
    }
}