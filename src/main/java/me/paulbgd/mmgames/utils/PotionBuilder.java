package me.paulbgd.mmgames.utils;

import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class PotionBuilder {

   private final PotionType type;
   private final boolean extended;
   private final int level;

   public PotionBuilder(PotionType type, boolean extended, int level) {
      this.type = type;
      this.extended = extended;
      this.level = level;
   }

   public PotionBuilder(PotionType type, boolean extended) {
      this(type, extended, 0);
   }

   public PotionBuilder(PotionType type, int level) {
      this(type, false, level);
   }

   public PotionBuilder(PotionType type) {
      this(type, 0);
   }

   public PotionType getType() {
      return this.type;
   }

   public boolean isExtended() {
      return this.extended;
   }

   public int getLevel() {
      return this.level;
   }

   public Potion build() {
      Potion potion = new Potion(type);
      potion.setLevel(this.level);
      if(potion.isSplash()) {
         potion.setHasExtendedDuration(this.extended);
      }
      return potion;
   }

}
