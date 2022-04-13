package fzzyhmstrs.structurized_reborn.impl;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fzzyhmstrs.structurized_reborn.api.FabricStructurePool;
import fzzyhmstrs.structurized_reborn.api.StructurePoolAddCallback;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.*;

public class FabricStructurePoolRegistry {

    private static final Multimap<String, Quintuple<String,String,String,String, Integer>> structures = LinkedHashMultimap.create();
    private static final Map<String,String> structures_key_ref = new HashMap<>();
    private static final Multimap<String, Pair<String,RegistryEntry<PlacedFeature>>> feature_structures = LinkedHashMultimap.create();

    public static void registerSimple(Identifier poolId,Identifier structureId, int weight){
        register(poolId,structureId,weight, StructureProcessorLists.EMPTY,StructurePool.Projection.RIGID, StructurePoolElementType.LEGACY_SINGLE_POOL_ELEMENT);
    }

    public static void register(Identifier poolId,Identifier structureId, int weight, RegistryEntry<StructureProcessorList> processor){
        register(poolId,structureId,weight,processor,StructurePool.Projection.RIGID,StructurePoolElementType.LEGACY_SINGLE_POOL_ELEMENT);
    }

    public static void register(Identifier poolId,Identifier structureId, int weight, RegistryEntry<StructureProcessorList> processor, StructurePool.Projection projection){
        register(poolId,structureId,weight,processor,projection,StructurePoolElementType.LEGACY_SINGLE_POOL_ELEMENT);
    }

    public static void register(Identifier poolId,Identifier structureId, int weight, RegistryEntry<StructureProcessorList> processor, StructurePool.Projection projection ,StructurePoolElementType<?> type){
        String poolType = Objects.requireNonNull(Registry.STRUCTURE_POOL_ELEMENT.getId(type)).toString();
        String processorId = Objects.requireNonNull(BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.getId(processor.value())).toString();
        String projectionId = projection.getId();
        structures.put(poolId.toString(), new Quintuple<>(structureId.toString(), poolType, processorId, projectionId, weight));
        structures_key_ref.put(structureId.toString(),poolId.toString());
    }

    public static void register(Identifier poolId, Identifier structureId, int weight, StructurePool.Projection projection, RegistryEntry<PlacedFeature> entry){
        register(poolId,structureId,weight,StructureProcessorLists.EMPTY,projection,StructurePoolElementType.FEATURE_POOL_ELEMENT);
        feature_structures.put(poolId.toString(), new Pair<>(structureId.toString(),entry));
    }

    public static @Nullable Triple<String,String,String> getPoolStructureElementInfo(String id){
        String poolId = structures_key_ref.get(id);
        for (Quintuple<String,String,String,String, Integer> quint : structures.get(poolId)){
            if (quint.a.equals(id)){
                return Triple.of(quint.b, quint.c, quint.d);
            }
        }
        return null;
    }

    public static void processRegistry(FabricStructurePool structurePool){
        String poolId = structurePool.getUnderlyingPool().getId().toString();
            for (String key : structures.keys()){
                if (Objects.equals(key, poolId)){
                    structures.get(key).forEach( value -> addToPool(structurePool,value, key)
                    );
                }
            }
    }

    private static void addToPool(FabricStructurePool structurePool, Quintuple<String,String,String,String, Integer> quint, String key){
        List<StructurePoolElement> spe = new LinkedList<>();
        if (Objects.equals(Registry.STRUCTURE_POOL_ELEMENT.get(new Identifier(quint.b)), StructurePoolElementType.SINGLE_POOL_ELEMENT)){
            spe.add(StructurePoolElement.ofProcessedSingle(quint.a,RegistryEntry.of(BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.get(new Identifier(quint.c)))).apply(StructurePool.Projection.getById(quint.d)));
        } else if (Objects.equals(Registry.STRUCTURE_POOL_ELEMENT.get(new Identifier(quint.b)), StructurePoolElementType.LEGACY_SINGLE_POOL_ELEMENT)){
            spe.add(StructurePoolElement.ofProcessedLegacySingle(quint.a,RegistryEntry.of(BuiltinRegistries.STRUCTURE_PROCESSOR_LIST.get(new Identifier(quint.c)))).apply(StructurePool.Projection.getById(quint.d)));
        }else if (Objects.equals(Registry.STRUCTURE_POOL_ELEMENT.get(new Identifier(quint.b)), StructurePoolElementType.FEATURE_POOL_ELEMENT)){
            List<StructurePoolElement> finalSpe = new LinkedList<>();
            feature_structures.get(key).forEach(
                    value -> {if(value.getLeft().equals(quint.a)){
                        finalSpe.add(StructurePoolElement.ofFeature(value.getRight()).apply(StructurePool.Projection.getById(quint.d)));
                    }}
            );
            spe.addAll(finalSpe);
        } else {
            spe.add(StructurePoolElement.ofEmpty().apply(StructurePool.Projection.RIGID));
        }
        spe.forEach(value -> structurePool.addStructurePoolElement(value,quint.e));
    }

    static{
        StructurePoolAddCallback.EVENT.register(FabricStructurePoolRegistry::processRegistry);
    }

    private record  Quintuple<A, B, C, D, E>(A a, B b, C c, D d, E e) {


    }
}
