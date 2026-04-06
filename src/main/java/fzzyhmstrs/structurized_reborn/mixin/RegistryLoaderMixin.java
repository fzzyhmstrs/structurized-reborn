package fzzyhmstrs.structurized_reborn.mixin;

import com.mojang.serialization.Decoder;
import fzzyhmstrs.structurized_reborn.api.StructurePoolAddCallback;
import fzzyhmstrs.structurized_reborn.impl.FabricStructurePoolImpl;
import net.minecraft.registry.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;


@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {
    @Inject(method = "loadFromResource(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryOps$RegistryInfoGetter;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V", at = @At("TAIL"))
    private static <E> void load(ResourceManager resourceManager, RegistryOps.RegistryInfoGetter infoGetter, MutableRegistry<E> registry, Decoder<E> elementDecoder, Map<RegistryKey<?>, Exception> errors, CallbackInfo ci) {
        if (registry.getKey().equals(RegistryKeys.TEMPLATE_POOL)) {
            Optional<RegistryOps.RegistryInfo<StructureProcessorList>> optionalRegistryInfo = infoGetter.getRegistryInfo(RegistryKeys.PROCESSOR_LIST);
            if (optionalRegistryInfo.isEmpty()){
                return;
            }
            RegistryEntryLookup<StructureProcessorList> registryEntryLookup = optionalRegistryInfo.get().entryLookup();
            for (E registryEntry : registry.stream().toList()) {
                if (!(registryEntry instanceof StructurePool pool)) {
                    continue;
                }
                Identifier id = registry.getId(registryEntry);
                //System.out.println("successfully registered a callback");
                StructurePoolAddCallback.EVENT.invoker().onAdd(new FabricStructurePoolImpl(pool, id), registryEntryLookup);
            }
        }
    }
}
