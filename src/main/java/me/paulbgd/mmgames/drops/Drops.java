package me.paulbgd.mmgames.drops;

import java.util.ArrayList;
import java.util.List;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.drops.Drop.DropRarity;
import me.paulbgd.mmgames.game.MMGame;

import org.bukkit.Location;

public class Drops {

   private static List<Drop> low = new ArrayList<Drop>();
   private static List<Drop> medium = new ArrayList<Drop>();
   private static List<Drop> hard = new ArrayList<Drop>();

   public static void addDrop(Drop drop) {
      switch (drop.getRarity()) {
      case HIGH:
         hard.add(drop);
         break;
      case LOW:
         low.add(drop);
         break;
      case MEDIUM:
         medium.add(drop);
         break;
      }
   }

   public static void drop(MMGame game, DropRarity rarity) {
      List<Drop> chosen = null;
      switch (rarity) {
      case HIGH:
         chosen = hard;
         break;
      case LOW:
         chosen = low;
         break;
      case MEDIUM:
         chosen = medium;
         break;
      }
      if (chosen == null || chosen.isEmpty() || game.getDropLocations().isEmpty()) {
         return;
      }
      Drop drop = chosen.get(MMGames.random.nextInt(chosen.size()));
      Location loc = game.getDropLocations().get(MMGames.random.nextInt(game.getDropLocations().size()));
      loc.getWorld().dropItem(loc, drop.getItem().build());
   }

   public static void drop(MMGame game) {
      int random = MMGames.random.nextInt(100);
      if (random <= 50) {
         drop(game, DropRarity.LOW);
      } else if (random <= 85) {
         drop(game, DropRarity.MEDIUM);
      } else {
         drop(game, DropRarity.HIGH);
      }
   }

}
