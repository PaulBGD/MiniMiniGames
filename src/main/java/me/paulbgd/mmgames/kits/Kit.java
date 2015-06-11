package me.paulbgd.mmgames.kits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.paulbgd.mmgames.kits.Kits.ArmorPiece;
import me.paulbgd.mmgames.kits.Kits.ArmorType;
import me.paulbgd.mmgames.kits.Kits.Click;
import me.paulbgd.mmgames.player.MMPlayer;
import me.paulbgd.mmgames.utils.ItemBuilder;
import me.paulbgd.mmgames.utils.MMData;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class Kit extends MMData {

   private final String name;
   private final List<String> description;

   private final ItemBuilder item;
   private final List<ItemBuilder> items = new ArrayList<ItemBuilder>();
   private final ItemBuilder[] armor = new ItemBuilder[4];

   public Kit(ItemBuilder icon, String name, String... description) {
      this.item = icon.setTitle(name);
      for(String lore : description) {
         this.item.addLore(lore);
      }
      this.name = name;
      this.description = Arrays.asList(description);

      for (ArmorPiece piece : ArmorPiece.values()) {
         this.armor[piece.getId()] = new ItemBuilder(Material.AIR);
      }
      Kits.addKit(this);
   }

   public Kit(Material icon, String name, String... description) {
      this(new ItemBuilder(icon).setTitle(name), name, description);
   }

   public void spawn(MMPlayer player) {
      ItemStack[] armor = new ItemStack[4];
      for (int i = 0; i < this.armor.length; i++) {
         armor[i] = this.armor[i].build();
      }
      player.getPlayer().getInventory().setArmorContents(armor);
   }

   public abstract void onSpawn(MMPlayer player);

   public abstract boolean onClick(Click click);

   public void setArmorColor(ArmorPiece piece, Color color) {
      if (!armor[piece.getId()].getType().name().contains("LEATHER_")) {
         armor[piece.getId()].setType(piece.getMaterial(ArmorType.LEATHER));
      }
      this.armor[piece.getId()].setColor(color);
   }

   public void setArmorPiece(ArmorPiece piece, ArmorType type) {
      this.armor[piece.getId()].setType(type.getMaterial(piece));
   }

   public void setArmorPiece(ArmorPiece piece, ItemBuilder builder) {
      this.armor[piece.getId()] = builder;
   }

   public void addItem(ItemBuilder item) {
      this.items.add(item);
   }

   public List<ItemBuilder> getItems() {
      return this.items;
   }

   public ItemBuilder[] getArmor() {
      return this.armor;
   }

   public String getName() {
      return this.name;
   }

   public List<String> getDescription() {
      return this.description;
   }

   public ItemBuilder getDescriptionItem() {
      return this.item;
   }

}
