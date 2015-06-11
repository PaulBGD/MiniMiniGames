package me.paulbgd.mmgames.projectiles;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.paulbgd.mmgames.projectiles.events.BlockProjectileHitEvent;
import me.paulbgd.mmgames.projectiles.events.CustomProjectileHitEvent;
import me.paulbgd.mmgames.utils.Particles;
import me.paulbgd.mmgames.utils.TypedRunnable;
import net.minecraft.server.v1_7_R1.AxisAlignedBB;
import net.minecraft.server.v1_7_R1.Block;
import net.minecraft.server.v1_7_R1.Entity;
import net.minecraft.server.v1_7_R1.EntityFallingBlock;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.EnumMovingObjectType;
import net.minecraft.server.v1_7_R1.IProjectile;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.MinecraftServer;
import net.minecraft.server.v1_7_R1.MovingObjectPosition;
import net.minecraft.server.v1_7_R1.Vec3D;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/**
 * Projectile made from falling block entity.
 */
public class BlockProjectile extends EntityFallingBlock implements CustomProjectile, IProjectile {
    
    private EntityLiving                         shooter;
    private int                                  lastTick;
    private int                                  age;
    private String                               name;
    private List<Runnable>                       runnables      = new ArrayList<Runnable>();
    private List<TypedRunnable<BlockProjectile>> typedRunnables = new ArrayList<TypedRunnable<BlockProjectile>>();
    
    /**
     * Instantiates a new block projectile.
     * 
     * @param name
     * projectile name
     * @param loc
     * location of projectile (sets position of projectile and shoots in pitch
     * and yaw direction)
     * @param blockId
     * block id
     * @param data
     * damage value of block
     * @param shooter
     * projectile shooter
     * @param power
     * projectile power
     */
    public BlockProjectile(String name, Location loc, int blockId, int data, LivingEntity shooter, float power) {
        super(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(), Block.e(blockId), data);
        this.shooter = ((CraftLivingEntity) shooter).getHandle();
        this.name = name;
        lastTick = MinecraftServer.currentTick;
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
        this.dropItem = false;
    }
    
    /**
     * Instantiates a new block projectile.
     * 
     * @param name
     * projectile name
     * @param shooter
     * projectile shooter (it uses entity's location to set x, y, z, pitch and
     * yaw of projectile)
     * @param blockId
     * block id
     * @param data
     * damage value of block
     * @param power
     * projectile power
     */
    public BlockProjectile(String name, LivingEntity shooter, int blockId, int data, float power) {
        super(((CraftLivingEntity) shooter).getHandle().world, shooter.getLocation().getX(), shooter.getLocation().getX(), shooter.getLocation().getX(), Block.e(blockId), data);
        this.shooter = ((CraftLivingEntity) shooter).getHandle();
        this.name = name;
        lastTick = MinecraftServer.currentTick;
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
        this.dropItem = false;
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
        return EntityType.FALLING_BLOCK;
    }
    
    @Override
    public org.bukkit.entity.Entity getEntity() {
        return getBukkitEntity();
    }
    
    @Override
    public LivingEntity getShooter() {
        return (LivingEntity) shooter.getBukkitEntity();
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public void h() {
        C();
        int elapsedTicks = MinecraftServer.currentTick - this.lastTick;
        this.age += elapsedTicks;
        lastTick = MinecraftServer.currentTick;
        
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        this.motY -= 0.03999999910593033D;
        this.Y = j(this.locX, (this.boundingBox.b + this.boundingBox.e) / 2.0D, this.locZ);
        move(this.motX, this.motY, this.motZ);
        
        float f = 0.98F;
        
        if (this.onGround) {
            f = 0.5880001F;
            Block i = this.world.getType(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ));
            
            if (i != null) {
                f = i.frictionFactor * 0.98F;
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
        
        vec3d = world.getVec3DPool().create(locX, locY, locZ);
        vec3d1 = world.getVec3DPool().create(locX + motX, locY + motY, locZ + motZ);
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
            CustomProjectileHitEvent event = new BlockProjectileHitEvent(this, world.getWorld().getBlockAt(movingobjectposition.b, movingobjectposition.c, movingobjectposition.d), CraftBlock.notchToBlockFace(movingobjectposition.face), getMaterial(), getData());
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                Particles.displayBlockCrack(getBukkitEntity().getLocation(), Block.b(id), (byte) 0, 0F, 0F, 0F, 1F, 60);
                die();
            }
        }
        else if (movingobjectposition.entity != null && movingobjectposition.entity instanceof EntityLiving) {
            LivingEntity living = (LivingEntity) movingobjectposition.entity.getBukkitEntity();
            CustomProjectileHitEvent event = new BlockProjectileHitEvent(this, living, getMaterial(), getData());
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                Particles.displayBlockCrack(getBukkitEntity().getLocation(), Block.b(id), (byte) 0, 0F, 0F, 0F, 1F, 60);
                die();
            }
        }
        else if (this.onGround) {
            CustomProjectileHitEvent event = new BlockProjectileHitEvent(this, getBukkitEntity().getLocation().getBlock().getRelative(BlockFace.DOWN), BlockFace.UP, getMaterial(), getData());
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                Particles.displayBlockCrack(getBukkitEntity().getLocation(), Block.b(id), (byte) 0, 0F, 0F, 0F, 1F, 60);
                die();
            }
        }
        if (isAlive()) {
            for (Runnable r : runnables) {
                r.run();
            }
            for (TypedRunnable<BlockProjectile> r : typedRunnables) {
                r.run(this);
            }
        }
    }
    
    @Override
    public String getProjectileName() {
        return name;
    }
    
    /**
     * Gets the block id.
     * 
     * @return the block id
     */
    @SuppressWarnings("deprecation")
    public Material getMaterial() {
        return Material.getMaterial(Block.b(id));
    }
    
    /**
     * Gets the damage value of block.
     * 
     * @return the data
     */
    public int getData() {
        return data;
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
        typedRunnables.add((TypedRunnable<BlockProjectile>) r);
    }
    
    @Override
    public void removeTypedRunnable(TypedRunnable<? extends CustomProjectile> r) {
        typedRunnables.remove(r);
    }
}
