package fzzyhmstrs.structurized_reborn.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.structure.processor.StructureProcessorList;

/**
 * A callback for newly added structure pools.
 *
 * <p><strong>Word of warning</strong>: Mods may be editing on the structure pool from user configured data packs
 * instead of the builtin Minecraft or mod resources.
 *
 * <p>Example usage:
 * <pre>{@code
 * StructurePoolAddCallback.EVENT.register(structurePool -> {
 * 	if (structurePool.id().toString().equals("minecraft:village/common/butcher_animals")) {
 * 		structurePool.addStructurePoolElement(StructurePoolElement.ofLegacySingle("village/common/animals/pigs_1").apply(StructurePool.Projection.RIGID), 2);
 *    }
 * });}
 * </pre>
 */
public interface StructurePoolAddCallback {
    /**
     * Called when structure pools are reloaded at data pack reload time.
     */
    Event<StructurePoolAddCallback> EVENT = EventFactory.createArrayBacked(StructurePoolAddCallback.class,
            listeners -> (initialPool, registryEntryLookup) -> {
                for (StructurePoolAddCallback listener : listeners) {
                    listener.onAdd(initialPool, registryEntryLookup);
                }
            }
    );

    void onAdd(FabricStructurePool initialPool, RegistryEntryLookup<StructureProcessorList> registryEntryLookup);
}
