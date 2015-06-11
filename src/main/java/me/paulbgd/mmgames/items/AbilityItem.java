package me.paulbgd.mmgames.items;

import me.paulbgd.mmgames.player.MMPlayer;
import me.paulbgd.mmgames.utils.ItemBuilder;

import org.bukkit.inventory.ItemStack;

public abstract class AbilityItem {

   private final ItemBuilder item;

   public AbilityItem(ItemBuilder item) {
      this.item = item;
   }

   public ItemBuilder getItem() {
      return this.item;
   }

   public ItemStack getNewItemStack() {
      return this.item.build();
   }

   /**
    * On item click.
    * 
    * @return true if item is to be destroyed
    */
   public abstract boolean onClick(MMPlayer player);

}
