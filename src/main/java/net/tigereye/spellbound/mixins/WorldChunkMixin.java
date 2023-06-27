package net.tigereye.spellbound.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.TouchedBlocksPersistentState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {

    @Final
    @Shadow
    World world;

    @Inject(at = @At(value="HEAD"), method = "setBlockState")
    public void spellboundSetBlockStateMixin(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir){
        if(this.world != null && Spellbound.config.prospector.DETECT_ABUSE){
            MinecraftServer server = world.getServer();
            if(server != null) {
                TouchedBlocksPersistentState tbpState = TouchedBlocksPersistentState.getTouchedBlocksPersistentState(world.getServer());
                tbpState.TouchBlock(pos);
            }
        }
    }
}
