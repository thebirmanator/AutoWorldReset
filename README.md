# AutoWorldReset
Automatically manage the resetting of worlds such as the Nether, End, or any other world!

## Configuration
There is one configuration file found in the plugins folder, which contains the world, how often it resets, what to do when it resets, and what schematics to paste. See the configuration file provided for an example.

Configuration files:
- [config.yml](https://github.com/thebirmanator/AutoWorldReset/blob/master/src/main/resources/config.yml "Config.yml")

## Commands and Permissions
There is only one command and permission needed!

Permission | Description
--- | ---
`autoworldreset.forcereset` | Force resets a world as if it had reached the scheduled time via the command `/forcereset <world>`

## Tips
This plugin depends on `Multiverse-Core` and `WorldEdit` (and optionally `WorldBorder`).
Schematic files should stay in WorldEdit's folder. Please make sure they are in `.schem` format. You may paste more than one schematic per world.

## API 
Are you a developer that wants to extend the functionality of this plugin? Luckily, it has events that you can listen for! Both contain the Bukkit World involving it.

Events:
```java
WorldResetEvent
WorldFinishResetEvent
```
`WorldResetEvent` fires when the world regenerates.
`WorldFinishResetEvent` fires after the world generates, or after it finishes filling if `WorldBorder` is enabled.

Always make sure to register your listeners in your onEnable method! 😉
