package me.paulbgd.mmgames.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.paulbgd.mmgames.MMGames;
import me.paulbgd.mmgames.utils.ItemBuilder;

public class AbilityItems {

   private static final List<AbilityItem> items = new ArrayList<AbilityItem>();
   private static final HashMap<String, AbilityItem> specials = new HashMap<String, AbilityItem>();

   public static void addItem(AbilityItem item) {
      items.add(item);
   }

   public static List<AbilityItem> getItems() {
      return items;
   }

   public static List<AbilityItem> getRawItems() {
      List<AbilityItem> items = new ArrayList<AbilityItem>(specials.values());
      items.addAll(AbilityItems.items);
      return items;
   }

   public static ItemBuilder getRandomItem() {
      return items.get(MMGames.random.nextInt(items.size())).getItem();
   }

   public static AbilityItem getItem(String name) {
      return specials.get(name);
   }

   public static void addItem(AbilityItem item, String name) {
      specials.put(name, item);
   }

}
