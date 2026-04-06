package fzzyhmstrs.structurized_reborn.impl;

import com.mojang.datafixers.util.Pair;
import fzzyhmstrs.structurized_reborn.api.FabricStructurePool;
import fzzyhmstrs.structurized_reborn.mixin.StructurePoolAccessor;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;


public class FabricStructurePoolImpl implements FabricStructurePool {
    private final StructurePool pool;
    private final Identifier id;

    public FabricStructurePoolImpl(StructurePool pool, Identifier id) {
        this.pool = pool;
        this.id = id;
    }

    @Override
    public void addStructurePoolElement(StructurePoolElement element) {
        addStructurePoolElement(element, 1);
    }

    @Override
    public void addStructurePoolElement(StructurePoolElement element, int weight) {
        //adds to elementCounts list; minecraft makes these immutable lists, so we replace them with an array list
        StructurePoolAccessor pool = (StructurePoolAccessor) getUnderlyingPool();

        if (pool.getElementWeights() instanceof ArrayList) {
            pool.getElementWeights().add(Pair.of(element, weight));
        } else {
            List<Pair<StructurePoolElement, Integer>> list = new ArrayList<>(pool.getElementWeights());
            list.add(Pair.of(element, weight));
            pool.setElementWeights(list);
        }

        // adds to elements list
        for (int i = 0; i < weight; i++) {
            pool.getElements().add(element);
        }
    }

    @Override
    public StructurePool getUnderlyingPool() {
        return pool;
    }

    @Override
    public Identifier getId() {
        return id;
    }
}
