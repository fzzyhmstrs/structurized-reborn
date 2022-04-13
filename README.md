# Structurized
<p align="left">
<a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-brightgreen.svg"></a>
</p>

THIS IS A REWORK OF DRAYLAR'S STRUCTURIZED. See the original here: https://github.com/omega-mc/structurized/tree/1.18

Structurized is a simple library focused on providing utilities for structures, features, jigsaws, and other world gen.

### Jigsaw Modification
Structurized currently provides a callback that allows you to modify `StructurePool`s in jigsaws such as villages. Say we wanted to add `village/desert/houses/desert_small_house_1` to the plains house pool:
```java
StructurePoolAddCallback.EVENT.register(structurePool -> {
    if(structurePool.getUnderlying().getId().toString().equals("minecraft:village/plains/houses")) {
        structurePool.addStructurePoolElement(new SinglePoolElement("village/desert/houses/desert_small_house_1"), 50);
    }
});
```
