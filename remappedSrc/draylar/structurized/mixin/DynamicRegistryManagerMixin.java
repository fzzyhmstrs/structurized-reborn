package draylar.structurized.mixin;

import draylar.structurized.api.StructurePoolAddCallback;
import draylar.structurized.impl.FabricStructurePoolImpl;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;


@Mixin(DynamicRegistryManager.class)
public abstract class DynamicRegistryManagerMixin {
    @Inject(method = "load(Lnet/minecraft/util/dynamic/RegistryOps;Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/util/registry/DynamicRegistryManager$Info;)V", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static <E> void load(RegistryOps<?> ops, DynamicRegistryManager manager, DynamicRegistryManager.Info<E> info, CallbackInfo ci, RegistryKey<? extends Registry<E>> registryKey) {
        if (registryKey.equals(Registry.STRUCTURE_POOL_KEY)) {
            for (E registryEntry : manager.get(registryKey)) {
                if (registryEntry instanceof StructurePool pool) {
                    StructurePoolAddCallback.EVENT.invoker().onAdd(new FabricStructurePoolImpl(pool));
                }
            }
        }
    }
}