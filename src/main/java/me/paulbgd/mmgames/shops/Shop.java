package me.paulbgd.mmgames.shops;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.player.MMPlayer;
import me.paulbgd.mmgames.utils.ItemBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Shop implements Listener {

   private Inventory inv;

   public Shop(String name, int rows) {
      this.inv = Bukkit.createInventory(null, (rows * 9) + 9, ChatColor.GOLD + "" + ChatColor.BOLD + name);
      MMGames.getPlugin().getServer().getPluginManager().registerEvents(this, MMGames.getPlugin());
   }

   public Shop addItem(ItemStack item) {
      this.inv.addItem(item);
      return this;
   }

   public Shop addItem(ItemBuilder item) {
      return this.addItem(item.build());
   }

   public Shop setItem(ItemStack item, int slot) {
      inv.setItem(slot, item);
      return this;
   }

   public Shop setItem(ItemStack item, int column, int row) {
      inv.setItem((row * 9) + column, item);
      return this;
   }

   public Shop clear() {
      inv.clear();
      return this;
   }

   public void destroy() {
      HandlerList.unregisterAll(this);
      inv.clear();
      this.inv = null;
   }

   public Inventory getInventory() {
      return this.inv;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getInventory().getTitle().equals(this.inv.getTitle()) && event.getCurrentItem() != null) {
         event.setCancelled(true);
         event.getWhoClicked().closeInventory();
         this.onClick(MMGames.getPlugin().getPlayer((Player) event.getWhoClicked()), event.getCurrentItem());
      }
   }

   public abstract void onClick(MMPlayer player, ItemStack item);
}
