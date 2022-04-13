package fzzyhmstrs.structurized_reborn.mixin;

import fzzyhmstrs.structurized_reborn.impl.FabricStructurePoolRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.dynamic.RegistryOps;
import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PoolStructurePiece.class)
public class PoolStructurePieceMixin {

    @Shadow @Final protected StructurePoolElement poolElement;

    @Inject(method = "writeNbt", at = @At(value = "TAIL"))
    private void fixPoolElement(StructureContext context, NbtCompound nbt, CallbackInfo ci){
        if (!nbt.contains("pool_element")) {
            RegistryOps<NbtElement> dynamicOps = RegistryOps.of(NbtOps.INSTANCE, context.registryManager());
            NbtCompound nbtEl = new NbtCompound();
            String poolId = poolElement.toString();
            String poolId2 = poolId.replace("\\]","");
            String split = "\\[";
            String[] poolIdArray = poolId2.split(split);
            String poolLocation = poolIdArray[poolIdArray.length - 1];
            Triple<String, String, String> info;
            if ((info = FabricStructurePoolRegistry.getPoolStructureElementInfo(poolLocation)) != null){
                nbtEl.putString("element_type", info.getLeft());
                nbtEl.putString("location",poolLocation);
                nbtEl.putString("processors", info.getMiddle());
                nbtEl.putString("projection", info.getRight());
                nbt.put("pool_element",nbtEl);
            }
            System.out.println(nbt);
        }
    }

}
