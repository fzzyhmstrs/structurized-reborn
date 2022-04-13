# Structurized
[![](https://jitpack.io/v/Draylar/structurized.svg)](https://jitpack.io/#Draylar/structurized)
[![PyPI license](https://img.shields.io/pypi/l/ansicolortags.svg)](https://pypi.python.org/pypi/ansicolortags/)

Structurized is a simple library focused on providing utilities for structures, features, jigsaws, and other world gen.

### Installation

`build.gradle`:
```java
repositories {
	...
	maven { url 'https://jitpack.io' }
}
```
```groovy
dependencies {
	modImplementation 'com.github.Draylar:structurized:1.16-SNAPSHOT'
}
```


### Jigsaw Modification
Structurized currently provides a callback that allows you to modify `StructurePool`s in jigsaws such as villages. Say we wanted to add `village/desert/houses/desert_small_house_1` to the plains house pool:
```java
StructurePoolAddCallback.EVENT.register(structurePool -> {
    if(structurePool.getUnderlying().getId().toString().equals("minecraft:village/plains/houses")) {
        structurePool.addStructurePoolElement(new SinglePoolElement("village/desert/houses/desert_small_house_1"), 50);
    }
});
```
