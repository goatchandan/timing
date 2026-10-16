# Vault Snipe (Fabric 26.2, client-side)

Auto-uses your ominous trial key on an ominous vault on the exact tick the
vault's displayed item becomes a heavy core.

## Use
1. Hold an **ominous trial key** in your main hand.
2. Look at the ominous vault.
3. The mod right-clicks it the tick the heavy core shows up.

Client-only: install in your own `mods` folder (needs Fabric API). Nothing
goes on the server. Check server rules first; automated clicks can be
flagged by anti-cheat.

## Build
Requires JDK 25.

Easiest: generate a Fabric 26.2 template at https://fabricmc.net/develop/template
(gives you the Gradle wrapper), then replace its `build.gradle`,
`gradle.properties`, `fabric.mod.json` and add the Java class from this
folder. Then:

    ./gradlew build

The jar is in `build/libs/` (use the one without `-sources`).

## If it fails to compile
26.2 is new; the likeliest spot is `vault.getSharedData().getDisplayItem()`.
Look at VaultBlockEntity / VaultSharedData in your IDE and adjust.
Version numbers in gradle.properties may also need bumping to the latest
Fabric API for 26.2.
