package fzzyhmstrs.structurized_reborn.impl;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fzzyhmstrs.structurized_reborn.api.FabricStructurePool;
import fzzyhmstrs.structurized_reborn.api.StructurePoolAddCallback;
import net.minecraft.structure.pool.ListPoolElement;
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
import java.util.function.Function;

public class FabricStructurePoolRegistry {

    private static final Multimap<String, Quintuple<String,String,String,String, Integer>> structures_info = LinkedHashMultimap.create();
    private static final Map<String,String> structures_key_ref = new HashMap<>();
    private static final Multimap<String, Pair<String,RegistryEntry<PlacedFeature>>> feature_structures = LinkedHashMultimap.create();
    private static final Multimap<String, ListPoolElement> list_structures = LinkedHashMultimap.create();

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
        structures_info.put(poolId.toString(), new Quintuple<>(structureId.toString(), poolType, processorId, projectionId, weight));
        structures_key_ref.put(structureId.toString(),poolId.toString());
    }

    public static void registerFeature(Identifier poolId, Identifier structureId, int weight, StructurePool.Projection projection, RegistryEntry<PlacedFeature> entry){
        register(poolId,structureId,weight,StructureProcessorLists.EMPTY,projection,StructurePoolElementType.FEATURE_POOL_ELEMENT);
        feature_structures.put(poolId.toString(), new Pair<>(structureId.toString(),entry));
    }

    public static void registerList(Identifier poolId, int weight, ListPoolElement listPoolElement){
        register(poolId,new Identifier("minecraft:air"),weight,StructureProcessorLists.EMPTY, StructurePool.Projection.RIGID,StructurePoolElementType.LIST_POOL_ELEMENT);
        list_structures.put(poolId.toString(), listPoolElement);
    }

    public static class ListPool {

        private StructurePool.Projection projection;
        private final List<Pair<String, RegistryEntry<StructureProcessorList> >> structureList = new ArrayList<>();

        private ListPool(){
            projection = StructurePool.Projection.RIGID;
        }

        public static ListPool builder(){
            return new ListPool();
        }

        public ListPool addStructureElement(Identifier structureId){
            this.structureList.add(new Pair<>(structureId.toString(), StructureProcessorLists.EMPTY));
            return this;
        }

        public ListPool addStructureElement(Identifier structureId, RegistryEntry<StructureProcessorList> processor){
            this.structureList.add(new Pair<>(structureId.toString(), processor));
            return this;
        }

        public ListPool addProjection(StructurePool.Projection projection){
            this.projection = projection;
            return this;
        }

        public ListPoolElement buildListPool(){
            List<Function<StructurePool.Projection, ? extends StructurePoolElement>> listPoolList = new ArrayList<>();
            structureList.forEach(value -> listPoolList.add(StructurePoolElement.ofProcessedLegacySingle(value.getLeft(),value.getRight())));
            return StructurePoolElement.ofList(listPoolList).apply(projection);
        }
    }

    public static @Nullable Triple<String,String,String> getPoolStructureElementInfo(String id){
        String poolId = structures_key_ref.get(id);
        for (Quintuple<String,String,String,String, Integer> quint : structures_info.get(poolId)){
            if (quint.a.equals(id)){
                return Triple.of(quint.b, quint.c, quint.d);
            }
        }
        return null;
    }

    public static void processRegistry(FabricStructurePool structurePool){
        String poolId = structurePool.getUnderlyingPool().getId().toString();
        for (String key : structures_info.keys()){
            if (Objects.equals(key, poolId)){
                structures_info.get(key).forEach(value -> addToPool(structurePool,value, key)
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
        }else if (Objects.equals(Registry.STRUCTURE_POOL_ELEMENT.get(new Identifier(quint.b)), StructurePoolElementType.LIST_POOL_ELEMENT)){
            spe.addAll(list_structures.get(key));
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
