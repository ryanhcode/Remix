package dev.ryanhcode.remix.fabric;

import dev.ryanhcode.remix.Remix;
import net.fabricmc.api.ModInitializer;

public class RemixFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Remix.init();
    }
}