package me.paulbgd.mmgames.drops;

import me.paulbgd.mmgames.interfaces.ItemUser;
import me.paulbgd.mmgames.utils.ItemBuilder;

public class Drop implements ItemUser {

   private final ItemBuilder item;
   private final DropRarity rarity;

   public Drop(ItemBuilder item, DropRarity rarity) {
      this.item = item;
      this.rarity = rarity;
   }

   @Override
   public ItemBuilder getItem() {
      return this.item;
   }

   public DropRarity getRarity() {
      return this.rarity;
   }

   public enum DropRarity {
      LOW, MEDIUM, HIGH;
   }

}
