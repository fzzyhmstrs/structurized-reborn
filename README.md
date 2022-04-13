# Structurized
<p align="left">
<a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/License-MIT-brightgreen.svg"></a>
</p>

### THIS IS A REWORK OF DRAYLAR'S STRUCTURIZED. See the original here: https://github.com/omega-mc/structurized/tree/1.18

Structurized Reborn is a simple library focused on providing utilities for structures, features, jigsaws, and other world gen.

### Jigsaw Modification
Structurized currently provides a callback that allows you to modify `StructurePool`s in jigsaws such as villages. Say we wanted to add `village/plains/houses/plains_small_house_1` to the plains house pool. Simply register the new structure to the desired pool and give it a weight and some optional modifiers. Call these register methods in the same place you would call any other server-focused registry event (registering items or blocks, for example)
```kotlin
FabricStructurePoolRegistry.register(
    Identifier("minecraft:village/desert/houses"),
    Identifier("minecraft:village/plains/houses/plains_small_house_1"),
    2, 
    StructureProcessorLists.MOSSIFY_10_PERCENT)
```

### Flexible registration
The register method is quite flexible, with several optional parameters to use as needed. In many cases you will be OK using the `registerSimple` method, but the main `register` method can be useful for doing something like adding the random mossy cobblestone that many village strcutres have, for example.

Parameters:
`poolId`: required, the target pool of structures to modify
`structureId`: required, the new structure nbt location identifier
`weight`: required, the probability of a structure being chosen for generation. A weight of 1 to 3 is about 1 structure per village
`processor`: optional, defines custom generation tweaks to apply, like random mossy cobblestone
`projection`: optional, defines the way the structure interacts with the ground (rigid in space or conform to the landscape)
`type`: optional, defines the type of `structurePoolElement` you want. This isn't needed the majority of the time
