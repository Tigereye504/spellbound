package net.tigereye.spellbound.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.Spellbound;

public class VectorUtil {

    public static Vec3 getEntityBoundingBoxOffset(Vec3 direction, AABB boundingBox){
        Vec3 borders = new Vec3(
                direction.x > 0 ? boundingBox.getXsize()/2 : -boundingBox.getXsize()/2,
                0,
                direction.z > 0 ? boundingBox.getZsize()/2 : -boundingBox.getZsize()/2);
        BlockExitInfo info = getHorizontalExitPoint(Vec3.ZERO,direction,borders);
        if(info != null) {
            return info.pos;
        }
        else{
            return Vec3.ZERO;
        }
    }
    public static BlockExitInfo getHorizontalExitPoint(Vec3 position, Vec3 velocity, Vec3 borders){
        double timeToImpactX;
        velocity = velocity.multiply(1,0,1);
        if(velocity.x != 0){
            timeToImpactX = (borders.x-position.x)/ velocity.x;
        }
        else{timeToImpactX = Double.POSITIVE_INFINITY;}

        double timeToImpactZ;
        if(velocity.z != 0){
            timeToImpactZ = (borders.z-position.z)/ velocity.z;
        }
        else{timeToImpactZ = Double.POSITIVE_INFINITY;}

        double timeToImpact;
        BlockExitInfo output = new BlockExitInfo();
        if(Double.isInfinite(timeToImpactX) && Double.isInfinite(timeToImpactZ)){
            return null;
        }
        if(timeToImpactX <= timeToImpactZ){
            //x intersects first
            timeToImpact = timeToImpactX;
            output.direction = (velocity.x >= 0) ? Direction.EAST : Direction.WEST;
        }
        else{
            //z intersects first
            timeToImpact = timeToImpactZ;
            output.direction = (velocity.z >= 0) ? Direction.SOUTH : Direction.NORTH;
        }

        output.pos = position.add(velocity.scale(timeToImpact));
        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info("TTX: "+timeToImpactX+" TTZ: "+timeToImpactZ);
            Spellbound.LOGGER.info("Border crossed: "+output.direction.name()+" "+output.pos.x()+","+output.pos.y()+","+output.pos.z());
        }
        return output;
    }

    public static Vec3 findCollisionWithStepAssistOnLine(Level world, Vec3 position, Vec3 direction, double length){
        int remainingMaxIterations = (int)(length*2+1);
        BlockPos blockPos = BlockPos.containing(position);
        Vec3 unitVector = direction.normalize();
        Vec3 finalPosition = position.add(unitVector.scale(length/*level*DISTANCE_PER_LEVEL*/));
        BlockPos finalBlockPosition = BlockPos.containing(finalPosition);
        boolean endPointFound = false;
        while(!endPointFound) {
            //find point and direction where next block is touched
            Vec3 borders = new Vec3(unitVector.x > 0 ? blockPos.getX()+1 : blockPos.getX(), blockPos.getY(), unitVector.z > 0 ? blockPos.getZ()+1 : blockPos.getZ());
            if(Spellbound.DEBUG){
                Spellbound.LOGGER.info("Tracked Borders: "+borders.x()+","+borders.y()+","+borders.z());
            }
            VectorUtil.BlockExitInfo exitInfo = VectorUtil.getHorizontalExitPoint(position,unitVector,borders);
            if(exitInfo == null){
                return position;
            }
            position = exitInfo.pos;
            blockPos = blockPos.relative(exitInfo.direction);
            if(Spellbound.DEBUG){
                Spellbound.LOGGER.info("Leap line entering block "+blockPos.getX()+","+blockPos.getY()+","+blockPos.getZ());
            }
            //check for obstruction, attempt to go over it
            if(SpellboundUtil.isPositionObstructed(world,blockPos)){
                blockPos = blockPos.relative(Direction.UP);
                if(SpellboundUtil.isPositionObstructed(world,blockPos)){
                    endPointFound = true;
                    //blockPos = blockPos.offset(exitInfo.direction,-1);
                    //position = new Vec3d(blockPos.getX()+.5, blockPos.getY()-1, blockPos.getZ()+.5 );
                }
                else{
                    //move position and end position up one block
                    position = new Vec3(position.x,Math.floor(position.y+1)+.1,position.z);
                    finalPosition = new Vec3(finalPosition.x, Math.floor(finalPosition.y+1)+.1, finalPosition.z);
                    finalBlockPosition = finalBlockPosition.offset(0,1,0);
                }
            }
            //check if finalBlockPosition has been reached
            if(!endPointFound && blockPos.equals(finalBlockPosition)){
                //if so, set position to finalPosition and get ready to warp
                endPointFound = true;
                position = finalPosition;
            }
            //finally, a sanity check to make sure we don't fly away into space somehow
            remainingMaxIterations--;
            if(remainingMaxIterations <= 0){
                Spellbound.LOGGER.error("Leap hit max iterations.");
                endPointFound = true;
            }
        }
        if(Double.isNaN(position.x)||Double.isNaN(position.y)||Double.isNaN(position.z)){
            Spellbound.LOGGER.error("Position returned NaN, that is very dangerous! Please report to dev.");
            return null;
        }
        return position;
    }


    public static Vec3 backtrackToUsableSpace(Level world, LivingEntity entity, AABB boundingBox, Vec3 position) {
        Vec3 moveVector = position.subtract(entity.position());
        Vec3 moveVectorNorm = moveVector.normalize();
        AABB newBounds = AABB.ofSize(boundingBox.getCenter().add(moveVector),boundingBox.getXsize(),boundingBox.getYsize(),boundingBox.getZsize());
        double length = moveVector.length();
        while(!world.noCollision(newBounds)){
            if(length < .5){
                return null;
            }
            length -= .5;
            newBounds = AABB.ofSize(entity.position().add(moveVectorNorm.scale(length)),
                    boundingBox.getXsize(),boundingBox.getYsize(),boundingBox.getZsize());
        }
        return entity.position().add(moveVectorNorm.scale(length));
    }

    public static Vec3 roundVectorAxis(Vec3 pos,Direction.Axis axis){
        switch(axis){
            case X:
                return new Vec3(Math.round(pos.x),pos.y,pos.z);
            case Y:
                return new Vec3(pos.x,Math.round(pos.y),pos.z);
            case Z:
                return new Vec3(pos.x,pos.y,Math.round(pos.z));
        }
        Spellbound.LOGGER.warn("Vector rounding failed, axis not found.");
        return pos;
    }

    public static class BlockExitInfo{
        public Vec3 pos;
        public Direction direction;
    }
}
