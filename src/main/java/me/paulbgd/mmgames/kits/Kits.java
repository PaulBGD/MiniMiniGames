package me.paulbgd.mmgames.kits;

import java.util.ArrayList;
import java.util.List;

import me.paulbgd.mmgames.player.MMPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class Kits {

   private static final List<Kit> kits = new ArrayList<Kit>();

   public static void addKit(Kit kit) {
      kits.add(kit);
   }

   public static List<Kit> getKits() {
      return kits;
   }

   public static class Click {

      private final MMPlayer player;
      private final Action action;
      private final ItemStack hand;

      public Click(MMPlayer player, Action action) {
         this.player = player;
         this.action = action;
         this.hand = player.getPlayer().getItemInHand();
      }

      public MMPlayer getPlayer() {
         return this.player;
      }

      public Action getAction() {
         return this.action;
      }

      public ItemStack getItem() {
         return this.hand;
      }

      public boolean isRightClick() {
         return this.action.name().contains("RIGHT");
      }

      public boolean isLeftClick() {
         return this.action.name().contains("LEFT");
      }
   }

   public enum ArmorPiece {
      HELMET(3), CHESTPLATE(2), LEGGINGS(1), BOOTS(0);

      private final int id;

      private ArmorPiece(int id) {
         this.id = id;
      }

      public Material getMaterial(ArmorType type) {
         return Kits.getMaterial(this, type);
      }

      public int getId() {
         return this.id;
      }
   }

   public enum ArmorType {
      LEATHER, IRON, GOLD, CHAINMAIL, DIAMOND;

      public Material getMaterial(ArmorPiece piece) {
         return Kits.getMaterial(piece, this);
      }
   }

   private static Material getMaterial(ArmorPiece piece, ArmorType type) {
      Bukkit.getLogger().info(new StringBuilder().append(type.name()).append('_').append(piece.name()).toString());
      return Material.getMaterial(new StringBuilder().append(type.name()).append('_').append(piece.name()).toString());
   }

}
