package me.paulbgd.mmgames.projectiles;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.paulbgd.mmgames.projectiles.events.CustomProjectileHitEvent;
import me.paulbgd.mmgames.utils.TypedRunnable;
import net.minecraft.server.v1_7_R1.AxisAlignedBB;
import net.minecraft.server.v1_7_R1.Entity;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.EnumMovingObjectType;
import net.minecraft.server.v1_7_R1.IProjectile;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.MinecraftServer;
import net.minecraft.server.v1_7_R1.MovingObjectPosition;
import net.minecraft.server.v1_7_R1.Vec3D;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

/**
 * Projectile made from every possible entity passed as parameter. Entity is moved in 1 tick scheduler.
 * @author stirante
 *
 */
public class ProjectileScheduler implements Runnable, IProjectile, CustomProjectile {
    
    private String       name;
    private EntityLiving shooter;
    private Entity       e;
    private Random       random;
    private int age;
    private int lastTick;
    private int id;
    private List<Runnable>                       runnables      = new ArrayList<Runnable>();
    private List<TypedRunnable<ProjectileScheduler>> typedRunnables = new ArrayList<TypedRunnable<ProjectileScheduler>>();
    /**
     * Creates new scheduler projectile
     * @param name projectile name used in events
     * @param e parent entity
     * @param shooter shooter
     * @param power shoot power
     * @param plugin plugin instance used to schedule task
     */
    public ProjectileScheduler(String name, org.bukkit.entity.Entity e, LivingEntity shooter, float power, Plugin plugin) {
        this.name = name;
        this.shooter = ((CraftLivingEntity) shooter).getHandle();
        this.e = ((CraftEntity) e).getHandle();
        try {
            Field f = Entity.class.getDeclaredField("random");
            f.setAccessible(true);
            random = (Random) f.get(this.e);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        lastTick = MinecraftServer.currentTick;
        this.e.setPositionRotation(shooter.getLocation().getX(), shooter.getLocation().getY(), shooter.getLocation().getZ(), shooter.getLocation().getYaw(), shooter.getLocation().getPitch());
        this.e.locX -= (MathHelper.cos(this.e.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.e.locY -= 0.10000000149011612D;
        this.e.locZ -= (MathHelper.sin(this.e.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.e.setPosition(this.e.locX, this.e.locY, this.e.locZ);
        this.e.height = 0.0F;
        float f = 0.4F;
        this.e.motX = (-MathHelper.sin(this.e.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.e.pitch / 180.0F * 3.1415927F) * f);
        this.e.motZ = (MathHelper.cos(this.e.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.e.pitch / 180.0F * 3.1415927F) * f);
        this.e.motY = (-MathHelper.sin(this.e.pitch / 180.0F * 3.1415927F) * f);
        shoot(this.e.motX, this.e.motY, this.e.motZ, power * 1.5F, 1.0F);
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 1, 1);
    }
    
    @Override
    public void run() {
        int elapsedTicks = MinecraftServer.currentTick - lastTick;
        age += elapsedTicks;
        lastTick = MinecraftServer.currentTick;
        
//        float f = 0.98F;
//        
//        if (e.onGround) {
//            f = 0.5880001F;
//            Block i = e.world.getType(MathHelper.floor(e.locX), MathHelper.floor(e.boundingBox.b) - 1, MathHelper.floor(e.locZ));
//            
//            if (i != null) {
//                f = i.frictionFactor * 0.98F;
//            }
//        }
//        
//        e.motX *= f;
//        e.motY *= 0.9800000190734863D;
//        e.motZ *= f;
//        if (e.onGround) {
//            e.motY *= -0.5D;
//        }
        
        if (this.age >= 1000) {
            e.die();
            Bukkit.getScheduler().cancelTask(id);
        }
        
        Vec3D vec3d = e.world.getVec3DPool().create(e.locX, e.locY, e.locZ);
        Vec3D vec3d1 = e.world.getVec3DPool().create(e.locX + e.motX, e.locY + e.motY, e.locZ + e.motZ);
        MovingObjectPosition movingobjectposition = e.world.a(vec3d, vec3d1);
        
        vec3d = e.world.getVec3DPool().create(e.locX, e.locY, e.locZ);
        vec3d1 = e.world.getVec3DPool().create(e.locX + e.motX, e.locY + e.motY, e.locZ + e.motZ);
        if (movingobjectposition != null) vec3d1 = e.world.getVec3DPool().create(movingobjectposition.pos.c, movingobjectposition.pos.d, movingobjectposition.pos.e);
        
        if (!e.world.isStatic) {
            Entity entity = null;
            List<?> list = e.world.getEntities(e, e.boundingBox.a(e.motX, e.motY, e.motZ).grow(1.5D, 1.5D, 1.5D));
            double d0 = 0.0D;
            
            for (int j = 0; j < list.size(); ++j) {
                Entity entity1 = (Entity) list.get(j);
                
                if (entity1.L() && entity1 != shooter) {
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
            CustomProjectileHitEvent event = new CustomProjectileHitEvent(this, e.world.getWorld().getBlockAt(movingobjectposition.b, movingobjectposition.c, movingobjectposition.d), CraftBlock.notchToBlockFace(movingobjectposition.face));
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                e.die();
                Bukkit.getScheduler().cancelTask(id);
                
            }
        }
        else if (movingobjectposition.entity != null && movingobjectposition.entity instanceof EntityLiving) {
            LivingEntity living = (LivingEntity) movingobjectposition.entity.getBukkitEntity();
            CustomProjectileHitEvent event = new CustomProjectileHitEvent(this, living);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                e.die();
                Bukkit.getScheduler().cancelTask(id);
            }
        }
        else if (e.onGround) {
            CustomProjectileHitEvent event = new CustomProjectileHitEvent(this, e.getBukkitEntity().getLocation().getBlock().getRelative(BlockFace.DOWN), BlockFace.UP);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                e.die();
                Bukkit.getScheduler().cancelTask(id);
            }
        }
        if (e.isAlive()) {
            for (Runnable r : runnables) {
                r.run();
            }
            for (TypedRunnable<ProjectileScheduler> r : typedRunnables) {
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
        e.motX = d0;
        e.motY = d1;
        e.motZ = d2;
        float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        
        e.lastYaw = e.yaw = (float) (Math.atan2(d0, d2) * 180.0D / 3.1415927410125732D);
        e.lastPitch = e.pitch = (float) (Math.atan2(d1, f3) * 180.0D / 3.1415927410125732D);
    }
    
    @Override
    public EntityType getEntityType() {
        return e.getBukkitEntity().getType();
    }
    
    @Override
    public org.bukkit.entity.Entity getEntity() {
        return e.getBukkitEntity();
    }
    
    @Override
    public LivingEntity getShooter() {
        return (LivingEntity) shooter.getBukkitEntity();
    }
    
    @Override
    public String getProjectileName() {
        return name;
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
    public boolean isInvulnerable() {
        return e.isInvulnerable();
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
        typedRunnables.add((TypedRunnable<ProjectileScheduler>) r);
    }
    
    @Override
    public void removeTypedRunnable(TypedRunnable<? extends CustomProjectile> r) {
        typedRunnables.remove(r);
    }
    
}
