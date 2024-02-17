package com.jeff_media.simpleblockwhitelist;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SimpleBlockWhitelist extends JavaPlugin implements Listener {

    private final Set<Material> globalWhitelist = new HashSet<>();

    @Nullable
    private String getNoPermissionMsg() {
        String message = getConfig().getString("noPermission", "You don't have permission to place or break this block");
        if(message.isEmpty()) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }



    private String getSendTo() {
        return getConfig().getString("sendTo", "chat").toLowerCase();
    }

    private boolean getOpsCanBypass() {
        return getConfig().getBoolean("opsCanBypass", false);
    }

    @Override
    public void onEnable() {
        registerPermissions();
        getServer().getPluginManager().registerEvents(this, this);
        reload();
    }

    private void registerPermissions() {
        Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .map(material -> material.name().toLowerCase())
                .forEach(this::registerPermission);
    }

    private void registerPermission(String matName) {
        try {
            Permission perm = new Permission("blocks." + matName, PermissionDefault.FALSE);
            getServer().getPluginManager().addPermission(perm);
        } catch (Exception ignored) {}
    }

    private void reload() {
        saveDefaultConfig();
        reloadConfig();
        globalWhitelist.clear();
        getConfig().getStringList("whitelist").stream()
                .map(String::toUpperCase)
                .map(Material::matchMaterial)
                .forEach(material -> {
                    if (material != null) globalWhitelist.add(material);
                });
    }

    private boolean isAllowed(Player player, Material type) {
        return globalWhitelist.contains(type) || player.hasPermission("blocks." + type.name().toLowerCase());
    }

    public void checkAndCancel(Player player, Material type, Cancellable event) {
        if (player.isOp() && getOpsCanBypass()) {
            return;
        }

        if (!isAllowed(player, type)) {
            sendMessage(player);
            event.setCancelled(true);
        }
    }

    private void sendMessage(Player player) {
        String msg = getNoPermissionMsg();
        if(msg == null) {
            return;
        }

        if (getSendTo().equals("chat")) {
            player.sendMessage(msg);
        } else if (getSendTo().equals("actionbar")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        checkAndCancel(event.getPlayer(), event.getBlock().getType(), event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        checkAndCancel(event.getPlayer(), event.getBlock().getType(), event);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        reload();
        sender.sendMessage(ChatColor.GRAY + "BlocksWhitelist reloaded. Globally whitelisted blocks are: " + String.join(", ", globalWhitelist.stream().map(Material::name).toArray(String[]::new)));
        return true;
    }
}
