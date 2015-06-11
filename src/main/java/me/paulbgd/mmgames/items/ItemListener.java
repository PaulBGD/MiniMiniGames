package me.paulbgd.mmgames.items;

import me.paulbgd.mmgames.events.MMListener;
import me.paulbgd.mmgames.kits.Kits.Click;
import me.paulbgd.mmgames.player.MMPlayer;
import me.paulbgd.mmgames.utils.Particles;

import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class ItemListener extends MMListener {

   public ItemListener() {
      super(ListenerType.RIGHT_CLICK);
   }

   @Override
   public boolean onEvent(Object[] data) {
      MMPlayer player = (MMPlayer) data[0];
      Action action = (Action) data[1];
      ItemStack itemstack = player.getPlayer().getItemInHand();
      if (itemstack == null) {
         return false;
      }
      for (AbilityItem item : AbilityItems.getRawItems()) {
         if (item.getItem().isItem(itemstack)) {
            if (item.onClick(player)) {
               Particles.displayIconCrack(player.getPlayer().getLocation(), itemstack.getTypeId(), 0.0f, 0.0f, 0.0f, 0.5f, 1, player.getPlayer());
               if (itemstack.getAmount() == 1) {
                  player.getPlayer().setItemInHand(null);
               } else {
                  itemstack.setAmount(itemstack.getAmount() - 1);
                  player.getPlayer().setItemInHand(itemstack);
               }
               player.getPlayer().updateInventory();
            }
            return true;
         }
      }
      // else check his kit
      if (player.hasKit()) {
         if (player.getKit().onClick(new Click(player, action))) {
            if (itemstack.getAmount() == 1) {
               player.getPlayer().setItemInHand(null);
            } else {
               itemstack.setAmount(itemstack.getAmount() - 1);
               player.getPlayer().setItemInHand(itemstack);
            }
            player.getPlayer().updateInventory();
         }
      }
      return true;
   }

}
