package fzzyhmstrs.structurized_reborn.mixin;

import fzzyhmstrs.structurized_reborn.impl.FabricStructurePoolRegistry;
import net.minecraft.registry.Registerable;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructurePools.class)
public class StructurePoolsMixin {

   /* @Inject(method = "bootstrap", at = @At(value = "HEAD"))
    private static void structurized_reborn_getBootstrapRegisterables(Registerable<StructurePool> structurePoolsRegisterable, CallbackInfo ci){
        System.out.println("Grabbed registerable");
        FabricStructurePoolRegistry.registryEntryLookup = structurePoolsRegisterable;
    }*/

}
