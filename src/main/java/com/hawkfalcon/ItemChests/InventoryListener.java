package com.hawkfalcon.ItemChests;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.hawkfalcon.ItemChests.API.PlayerItemChestOpenEvent;


public class InventoryListener implements Listener {
    public ItemChests p;

    public InventoryListener(ItemChests m) {
        this.p = m;
    }
    
    @EventHandler
    public void onOpen(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST && ((Chest) event.getClickedBlock()).getInventory().getName().equals(ChatColor.RESET + "ItemChest")) {
            PlayerItemChestOpenEvent e = new PlayerItemChestOpenEvent(event.getPlayer(), event.getClickedBlock().getLocation());
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(InventoryClickEvent event) {
        String n = event.getWhoClicked().getName();
        if (event.getInventory().getType() == InventoryType.CHEST && event.getInventory().getName().equals(ChatColor.RESET + "ItemChest")) {
            if (event.getSlotType() == SlotType.CONTAINER) {
                if (event.getRawSlot() < 27) {
                    if (event.isLeftClick()) {
                        if (!p.getServer().getPlayer(n).hasPermission("ic.add")) {
                            event.setCancelled(true);
                        }
                    } else {
                        if (!p.getServer().getPlayer(n).hasPermission("ic.recieve"))
                            return;
                        ItemStack i = event.getCurrentItem();
                        if (i.getTypeId() == 0)
                            return;
                        event.setCancelled(true);
                        if (p.infinite) {
                            recieveItem(n, i);
                        } else {
                            if (!p.playerLimit.containsKey(n)) {
                                p.playerLimit.put(n, p.limit);
                            } else {
                                if (p.playerLimit.get(n) > 0) {
                                    p.playerLimit.put(n, p.playerLimit.get(n) - 1);
                                    recieveItem(n, i);
                                } else {
                                    message("You have reached the max items for today", n);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void message(String message, String sender) {
        p.getServer().getPlayer(sender).sendMessage("[" + ChatColor.GREEN + "ItemChest" + ChatColor.WHITE + "] " + message);
    }

    public void recieveItem(String n, ItemStack i) {
        p.getServer().getPlayer(n).getInventory().addItem(i);
        message("Received " + i.getAmount() + " " + i.getType() + "!", n);
    }
}
