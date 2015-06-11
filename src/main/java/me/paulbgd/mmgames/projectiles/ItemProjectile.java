package me.paulbgd.mmgames.projectiles;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.paulbgd.mmgames.projectiles.events.CustomProjectileHitEvent;
import me.paulbgd.mmgames.projectiles.events.ItemProjectileHitEvent;
import me.paulbgd.mmgames.utils.Particles;
import me.paulbgd.mmgames.utils.TypedRunnable;
import net.minecraft.server.v1_7_R1.AxisAlignedBB;
import net.minecraft.server.v1_7_R1.Block;
import net.minecraft.server.v1_7_R1.Entity;
import net.minecraft.server.v1_7_R1.EntityHuman;
import net.minecraft.server.v1_7_R1.EntityItem;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.EnumMovingObjectType;
import net.minecraft.server.v1_7_R1.IProjectile;
import net.minecraft.server.v1_7_R1.Item;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.MinecraftServer;
import net.minecraft.server.v1_7_R1.MovingObjectPosition;
import net.minecraft.server.v1_7_R1.Vec3D;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

/**
 * Projectile made from item entity
 */
public class ItemProjectile extends EntityItem implements IProjectile, CustomProjectile {
    
    private EntityLiving shooter;
    private int          lastTick;
    private String       name;
    private List<Runnable>                       runnables      = new ArrayList<Runnable>();
    private List<TypedRunnable<ItemProjectile>> typedRunnables = new ArrayList<TypedRunnable<ItemProjectile>>();
    
    /**
     * Instantiates a new item projectile.
     * 
     * @param name
     * projectile name
     * @param loc
     * location of projectile (sets position of projectile and shoots in pitch
     * and yaw direction)
     * @param itemstack
     * item stack to shoot
     * @param shooter
     * projectile shooter
     * @param power
     * projectile power
     */
    public ItemProjectile(String name, Location loc, org.bukkit.inventory.ItemStack itemstack, LivingEntity shooter, float power) {
        super(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(itemstack));
        lastTick = MinecraftServer.currentTick;
        this.name = name;
        this.pickupDelay = Integer.MAX_VALUE;
        this.shooter = ((CraftLivingEntity) shooter).getHandle();
        this.a(0.25F, 0.25F);
        setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        locX -= (MathHelper.cos(yaw / 180.0F * 3.1415927F) * 0.16F);
        locY -= 0.10000000149011612D;
        locZ -= (MathHelper.sin(yaw / 180.0F * 3.1415927F) * 0.16F);
        setPosition(locX, locY, locZ);
        height = 0.0F;
        float f = 0.4F;
        motX = (-MathHelper.sin(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f);
        motZ = (MathHelper.cos(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f);
        motY = (-MathHelper.sin(pitch / 180.0F * 3.1415927F) * f);
        shoot(motX, motY, motZ, power * 1.5F, 1.0F);
        world.addEntity(this);
    }
    
    /**
     * Instantiates a new item projectile.
     * 
     * @param name
     * projectile name
     * @param shooter
     * projectile shooter (it uses entity's location to set x, y, z, pitch and
     * yaw of projectile)
     * @param itemstack
     * item stack to shoot
     * @param power
     * projectile power
     */
    public ItemProjectile(String name, LivingEntity shooter, org.bukkit.inventory.ItemStack item, float power) {
        super(((CraftLivingEntity) shooter).getHandle().world);
        lastTick = MinecraftServer.currentTick;
        this.name = name;
        this.pickupDelay = Integer.MAX_VALUE;
        setItemStack(CraftItemStack.asNMSCopy(item));
        this.shooter = ((CraftLivingEntity) shooter).getHandle();
        this.a(0.25F, 0.25F);
        setPositionRotation(shooter.getLocation().getX(), shooter.getLocation().getY() + shooter.getEyeHeight(), shooter.getLocation().getZ(), shooter.getLocation().getYaw(), shooter.getLocation().getPitch());
        locX -= (MathHelper.cos(yaw / 180.0F * 3.1415927F) * 0.16F);
        locY -= 0.10000000149011612D;
        locZ -= (MathHelper.sin(yaw / 180.0F * 3.1415927F) * 0.16F);
        setPosition(locX, locY, locZ);
        height = 0.0F;
        float f = 0.4F;
        motX = (-MathHelper.sin(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f);
        motZ = (MathHelper.cos(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F) * f);
        motY = (-MathHelper.sin(pitch / 180.0F * 3.1415927F) * f);
        shoot(motX, motY, motZ, power * 1.5F, 1.0F);
        world.addEntity(this);
    }

    int groundTimes = 0;
    
    @SuppressWarnings("rawtypes")
    @Override
    public void h() {
        C();
        int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
        this.pickupDelay -= elapsedTicks;
        this.age += elapsedTicks;
        lastTick = MinecraftServer.currentTick;
        
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        this.motY -= 0.03999999910593033D;
        this.Y = j(this.locX, (this.boundingBox.b + this.boundingBox.e) / 2.0D, this.locZ);
        move(this.motX, this.motY, this.motZ);
        boolean flag = ((int) this.lastX != (int) this.locX) || ((int) this.lastY != (int) this.locY) || ((int) this.lastZ != (int) this.locZ);
        
        if ((flag) || (this.ticksLived % 25 == 0)) {
            if (this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)) == net.minecraft.server.v1_7_R1.Block.b("lava")) {
                this.motY = 0.2000000029802322D;
                this.motX = ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                this.motZ = ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                makeSound("random.fizz", 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
            }
        }
        
        float f = 0.98F;
        
        if (this.onGround) {
            f = 0.5880001F;
            Block i = this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ));
            
            if (i != null) {
                f = i.frictionFactor * 0.98F;
            }
            if(this.groundTimes++ >= 3) {
               CustomProjectileHitEvent event = new ItemProjectileHitEvent(this, getBukkitEntity().getLocation().getBlock().getRelative(BlockFace.DOWN), BlockFace.UP, getItem());
               Bukkit.getPluginManager().callEvent(event);
               if (!event.isCancelled()) {
                   if (CraftItemStack.asCraftMirror(getItemStack()).getType().isBlock()) Particles.displayBlockCrack(getBukkitEntity().getLocation(), Item.b(getItemStack().getItem()), (byte) 0, 0F, 0F, 0F, 1F, 20);
                   else Particles.displayIconCrack(getBukkitEntity().getLocation(), Item.b(getItemStack().getItem()), 0F, 0F, 0F, 0.1F, 20);
                   die();
               }
            }
        }
        
        this.motX *= f;
        this.motY *= 0.9800000190734863D;
        this.motZ *= f;
        if (this.onGround) {
            this.motY *= -0.5D;
        }
        
        if (this.age >= 1000) {
            die();
        }

        Vec3D vec3d = world.getVec3DPool().create(locX, locY, locZ);
        Vec3D vec3d1 = world.getVec3DPool().create(locX + motX, locY + motY, locZ + motZ);
        MovingObjectPosition movingobjectposition = world.a(vec3d, vec3d1);

        if (movingobjectposition != null) vec3d1 = world.getVec3DPool().create(movingobjectposition.pos.c, movingobjectposition.pos.d, movingobjectposition.pos.e);
        
        if (!world.isStatic) {
            Entity entity = null;
            List list = world.getEntities(this, boundingBox.a(motX, motY, motZ).grow(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            EntityLiving entityliving = shooter;
            
            for (int j = 0; j < list.size(); ++j) {
                Entity entity1 = (Entity) list.get(j);
                
                if (entity1.L() && entity1 != entityliving) {
                    float f1 = 0.3F;
                    AxisAlignedBB axisalignedbb = entity1.boundingBox.grow(f1, f1, f1);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb.a(vec3d, vec3d1);
                    
                    if (movingobjectposition1 != null) {
                        double d1 = vec3d.distanceSquared(movingobjectposition1.pos);
                        
                        if (d1 < d0 || d0 == 0.0D) {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }
            
            if (entity != null) movingobjectposition = new MovingObjectPosition(entity);
        }
        
        if (movingobjectposition != null) if (movingobjectposition.type == EnumMovingObjectType.BLOCK) {
            CustomProjectileHitEvent event = new ItemProjectileHitEvent(this, world.getWorld().getBlockAt(movingobjectposition.b, movingobjectposition.c, movingobjectposition.d), CraftBlock.notchToBlockFace(movingobjectposition.face), getItem());
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                if (CraftItemStack.asCraftMirror(getItemStack()).getType().isBlock()) Particles.displayBlockCrack(getBukkitEntity().getLocation(), Item.b(getItemStack().getItem()), (byte) 0, 0F, 0F, 0F, 1F, 20);
                else Particles.displayIconCrack(getBukkitEntity().getLocation(), Item.b(getItemStack().getItem()), 0F, 0F, 0F, 0.1F, 20);
                die();
            }
        }
        else if (movingobjectposition.entity != null && movingobjectposition.entity instanceof EntityLiving) {
            LivingEntity living = (LivingEntity) movingobjectposition.entity.getBukkitEntity();
            CustomProjectileHitEvent event = new ItemProjectileHitEvent(this, living, getItem());
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                if (CraftItemStack.asCraftMirror(getItemStack()).getType().isBlock()) Particles.displayBlockCrack(getBukkitEntity().getLocation(), Item.b(getItemStack().getItem()), (byte) 0, 0F, 0F, 0F, 1F, 20);
                else Particles.displayIconCrack(getBukkitEntity().getLocation(), Item.b(getItemStack().getItem()), 0F, 0F, 0F, 0.1F, 20);
                die();
            }
        }
        else if (this.onGround) {
            CustomProjectileHitEvent event = new ItemProjectileHitEvent(this, getBukkitEntity().getLocation().getBlock().getRelative(BlockFace.DOWN), BlockFace.UP, getItem());
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                if (CraftItemStack.asCraftMirror(getItemStack()).getType().isBlock()) Particles.displayBlockCrack(getBukkitEntity().getLocation(), Item.b(getItemStack().getItem()), (byte) 0, 0F, 0F, 0F, 1F, 20);
                else Particles.displayIconCrack(getBukkitEntity().getLocation(), Item.b(getItemStack().getItem()), 0F, 0F, 0F, 0.1F, 20);
                die();
            }
        }
        if (isAlive()) {
            for (Runnable r : runnables) {
                r.run();
            }
            for (TypedRunnable<ItemProjectile> r : typedRunnables) {
                r.run(this);
            }
        }
    }
    
    @Override
    public void shoot(double d0, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        
        d0 /= f2;
        d1 /= f2;
        d2 /= f2;
        d0 += random.nextGaussian() * 0.007499999832361937D * f1;
        d1 += random.nextGaussian() * 0.007499999832361937D * f1;
        d2 += random.nextGaussian() * 0.007499999832361937D * f1;
        d0 *= f;
        d1 *= f;
        d2 *= f;
        motX = d0;
        motY = d1;
        motZ = d2;
        float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        
        lastYaw = yaw = (float) (Math.atan2(d0, d2) * 180.0D / 3.1415927410125732D);
        lastPitch = pitch = (float) (Math.atan2(d1, f3) * 180.0D / 3.1415927410125732D);
    }
    
    @Override
    public EntityType getEntityType() {
        return EntityType.DROPPED_ITEM;
    }
    
    @Override
    public org.bukkit.entity.Entity getEntity() {
        return getBukkitEntity();
    }
    
    @Override
    public LivingEntity getShooter() {
        return (LivingEntity) shooter.getBukkitEntity();
    }
    
    @Override
    public void b_(EntityHuman entityhuman) {
        if (entityhuman == shooter && age <= 3) return;
        LivingEntity living = entityhuman.getBukkitEntity();
        CustomProjectileHitEvent event = new ItemProjectileHitEvent(this, living, getItem());
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            if (CraftItemStack.asCraftMirror(getItemStack()).getType().isBlock()) Particles.displayBlockCrack(getBukkitEntity().getLocation(), Item.b(getItemStack().getItem()), (byte) 0, 0F, 0F, 0F, 1F, 20);
            else Particles.displayIconCrack(getBukkitEntity().getLocation(), Item.b(getItemStack().getItem()), 0F, 0F, 0F, 0.1F, 20);
            die();
        }
    }
    
    @Override
    public String getProjectileName() {
        return name;
    }
    
    /**
     * Gets the item.
     * 
     * @return the item
     */
    public ItemStack getItem() {
        return CraftItemStack.asCraftMirror(getItemStack());
    }
    
    @Override
    public void setInvulnerable(boolean value) {
        try {
            Field f = getClass().getDeclaredField("invulnerable");
            f.setAccessible(true);
            f.set(this, value);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    @Override
    public void addRunnable(Runnable r) {
        runnables.add(r);
    }
    
    @Override
    public void removeRunnable(Runnable r) {
        runnables.remove(r);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void addTypedRunnable(TypedRunnable<? extends CustomProjectile> r) {
        typedRunnables.add((TypedRunnable<ItemProjectile>) r);
    }
    
    @Override
    public void removeTypedRunnable(TypedRunnable<? extends CustomProjectile> r) {
        typedRunnables.remove(r);
    }
}