package me.paulbgd.mmgames.projectiles;

import me.paulbgd.mmgames.utils.TypedRunnable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/**
 * Custom projectile interface.
 */
public interface CustomProjectile {
    
    /**
     * Gets the entity type.
     * 
     * @return entity type of projectile
     */
    public EntityType getEntityType();
    
    /**
     * Gets the entity.
     * 
     * @return the entity
     */
    public Entity getEntity();
    
    /**
     * Gets the shooter.
     * 
     * @return the shooter
     */
    public LivingEntity getShooter();
    
    /**
     * Gets the projectile name.
     * 
     * @return the projectile name
     */
    public String getProjectileName();
    
    /**
     * Sets entity invulnerable.
     * 
     * @param value
     * invulnerable state
     */
    public void setInvulnerable(boolean value);
    
    /**
     * Checks if entity is invulnerable.
     * 
     * @return true, if entity is invulnerable
     */
    public boolean isInvulnerable();
    
    /**
     * Adds the runnable.
     * 
     * @param r
     * runnable
     */
    public void addRunnable(Runnable r);
    
    /**
     * Removes the runnable.
     * 
     * @param r
     * runnable
     */
    public void removeRunnable(Runnable r);
    
    /**
     * Adds the typed runnable.
     * 
     * @param r
     * runnable
     */
    public void addTypedRunnable(TypedRunnable<? extends CustomProjectile> r);
    
    /**
     * Removes the typed runnable.
     * 
     * @param r
     * runnable
     */
    public void removeTypedRunnable(TypedRunnable<? extends CustomProjectile> r);
    
}
