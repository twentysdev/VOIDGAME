package me.krokodil69u.voidgame

import me.krokodil69u.voidgame.commands.StartEventCMD
import me.krokodil69u.voidgame.commands.StartGameCMD
import me.krokodil69u.voidgame.commands.StopGameCMD
import me.krokodil69u.voidgame.events.Events
import me.krokodil69u.voidgame.other.EmptyChunkGenerator
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

class VOIDGAME : JavaPlugin() {
    var players = arrayListOf<Player>()
    var playerWins = hashMapOf<Player, Int>()
    var playing: Boolean = false
    var gameLoop: BukkitTask? = null
    lateinit var gameWorld: World
    override fun onEnable() {
        instance = this
        playing = false
        players = arrayListOf()

        val worldCreator = WorldCreator("VOIDGAME")
        worldCreator.generator(EmptyChunkGenerator())
        gameWorld = worldCreator.createWorld()!!

        getCommand("start")!!.setExecutor(StartGameCMD())
        getCommand("vgstop")!!.setExecutor(StopGameCMD())
        getCommand("startevent")!!.setExecutor(StartEventCMD())
        server.pluginManager.registerEvents(Events(), this)
    }

    override fun onDisable() {
    }

    companion object {
        @JvmStatic
        var instance: VOIDGAME? = null
            private set
    }
}
