package com.jeff_media.blockswhitelist

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class BlocksWhitelist : JavaPlugin(), Listener {

    private val globalWhitelist = mutableSetOf<Material>()
    private val noPermissionMsg
        get() = ChatColor.translateAlternateColorCodes(
            '&',
            config.getString("noPermission", "You don't have permission to place or break this block")!!
        )

    override fun onEnable() {
        saveDefaultConfig()
        server.pluginManager.registerEvents(this, this)
        reload()
    }

    private fun reload() {
        reloadConfig()
        globalWhitelist.clear()
        config.getStringList("whitelist").mapNotNull { Material.matchMaterial(it.uppercase()) }
            .forEach(globalWhitelist::add)
    }

    private fun isWhitelisted(player: Player, type: Material): Boolean {
        if (globalWhitelist.contains(type)) return true
        return player.hasPermission("blocks." + type.name.lowercase())
    }

    fun checkAndCancel(player: Player, type: Material, event: Cancellable) {
        if(!isWhitelisted(player, type)) {
            player.sendMessage(noPermissionMsg)
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockBreak(event: org.bukkit.event.block.BlockBreakEvent) {
        checkAndCancel(event.player, event.block.type, event)
    }

    @EventHandler
    fun onBlockPlace(event: org.bukkit.event.block.BlockPlaceEvent) {
        checkAndCancel(event.player, event.block.type, event)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        reload()
        sender.sendMessage(ChatColor.GRAY.toString() + "BlocksWhitelist reloaded. Globally whitelisted blocks are: " + globalWhitelist.joinToString(", "))
        return true
    }

}