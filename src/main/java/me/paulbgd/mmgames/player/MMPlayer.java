package me.paulbgd.mmgames.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import me.paulbgd.mmgames.events.MMEventHandler;
import me.paulbgd.mmgames.game.MMGame;
import me.paulbgd.mmgames.kits.Kit;
import me.paulbgd.mmgames.player.Ranks.Rank;
import me.paulbgd.mmgames.utils.MMData;
import me.paulbgd.mmgames.utils.Utils;
import net.minecraft.server.v1_7_R1.EntityCreature;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MMPlayer extends MMData {

   private static final Date date = new Date();

   private Player player;
   private Rank rank;

   private MMGame game;
   private Kit kit;

   private boolean spectating;

   public MMPlayer(Player player) {
      this.player = player;
      this.rank = Rank.DEFAULT;

      this.addData("stat_kills", 0);
      this.addData("stat_deaths", 0);
      this.addData("stat_wins", 0);
      this.addData("stat_losses", 0);
   }

   public Player getPlayer() {
      return this.player;
   }

   public void reset() {
      this.player.getInventory().setArmorContents(null);
      this.player.getInventory().clear();
      for (PotionEffect effect : this.player.getActivePotionEffects()) {
         this.player.removePotionEffect(effect.getType());
      }
      if (this.player.getMaxHealth() < 20) {
         this.player.setMaxHealth(20);
         this.player.setHealth(20);
      } else {
         this.player.setHealth(20);
         this.player.setMaxHealth(20);
      }
      this.player.setFoodLevel(20);
      this.player.setFlying(false);
      this.player.setAllowFlight(false);
   }

   public void updateInventory() {
      this.player.updateInventory();
   }

   public List<ItemStack> getAllItems() {
      List<ItemStack> items = new ArrayList<ItemStack>();
      items.addAll(Arrays.asList(this.player.getInventory().getArmorContents()));
      items.addAll(Arrays.asList(this.player.getInventory().getContents()));
      List<ItemStack> notNull = new ArrayList<ItemStack>();
      for (ItemStack item : items) {
         if (item == null) {
            continue;
         }
         notNull.add(item);
      }
      return notNull;
   }

   public void addStat(String name) {
      this.addData("stat_" + name, this.getInt("stat_" + name) + 1);
   }

   public void setKit(Kit kit) {
      this.kit = kit;
   }

   public Kit getKit() {
      return this.kit;
   }

   public boolean hasKit() {
      return this.kit != null;
   }

   public boolean inGame() {
      return this.game != null;
   }

   public void setGame(MMGame game) {
      this.game = game;
      date.setTime(new Date().getTime());
      if (game == null && this.hasData("time_joined")) {
         String name = "time_played";
         this.addData("stat_" + name, this.getInt("stat_" + name) + (date.getMinutes() - this.getInt("time_joined")));
      } else if (game != null) {
         this.addData("time_joined", date.getMinutes());
         this.spectating = false;
      }
   }

   public void setSpectating(boolean spectating) {
      this.spectating = spectating;
      if (spectating) {
         for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == this.player) {
               continue;
            }
            player.hidePlayer(this.player);
         }
         this.player.setAllowFlight(true);
         for (LivingEntity entity : this.player.getWorld().getLivingEntities()) {
            if (!(entity instanceof Creature)) {
               continue;
            }
            EntityCreature creature = (EntityCreature) ((CraftLivingEntity) entity).getHandle();
            if (creature.target == ((CraftPlayer) this.player).getHandle()) {
               creature.target = null;
            }
         }
         MMEventHandler.onStartSpectating(this);
      } else {
         this.player.setAllowFlight(false);
      }
   }

   public boolean isSpectating() {
      return this.spectating;
   }

   public void addLoss() {
      this.addStat("wins");
   }

   public void addWin() {
      this.addStat("losses");
   }

   public MMGame getGame() {
      return this.game;
   }

   public Rank getRank() {
      return this.rank;
   }

   public void setRank(Rank rank) {
      this.rank = rank;
   }

   public String getName() {
      return this.player.getName();
   }

   public void sendMessage(String message) {
      this.player.sendMessage(Utils.color(message));
   }

   public void sendMessage(String message, Object... args) {
      this.sendMessage(String.format(message, args));
   }

   public void sendWarning(String message) {
      this.sendMessage("[c]" + message);
   }

   public void addPotionEffect(PotionEffectType type, double duration) {
      this.addPotionEffect(type, duration, 0);
   }

   public void addPotionEffect(PotionEffectType type, double d, int boost) {
      this.player.addPotionEffect(type.createEffect(((int) d * 20), boost), true);
   }

}
