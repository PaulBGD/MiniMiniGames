package me.paulbgd.mmgames.utils;

import java.util.HashMap;

import me.paulbgd.mmgames.shops.Shop;

import org.bukkit.Location;

public class MMData {

   private HashMap<String, Object> data = new HashMap<String, Object>();

   public void addData(String name, Object dataObject) {
      if (this.data.containsKey(name)) {
         this.data.remove(name);
      }
      this.data.put(name, dataObject);
   }

   public void removeData(String name) {
      if (this.data.containsKey(name)) {
         this.data.remove(name);
      }
   }

   public boolean hasData(String name) {
      return this.data.containsKey(name);
   }

   public Object get(String name) {
      return this.data.get(name);
   }

   public String getString(String name) {
      return (String) this.data.get(name);
   }

   public int getInt(String name) {
      return (int) this.data.get(name);
   }

   public Location getLocation(String name) {
      return (Location) this.data.get(name);
   }

   public boolean getBoolean(String name) {
      return (boolean) this.data.get(name);
   }

   public float getFloat(String name) {
      return (float) this.data.get(name);
   }
   
   public long getLong(String name) {
      return (long) this.data.get(name);
   }

   public PlayerList getPlayerList(String name) {
      return (PlayerList) this.data.get(name);
   }

   public Shop getShop(String name) {
      return (Shop) this.data.get(name);
   }

   public void clearData() {
      data.clear();
   }

}
