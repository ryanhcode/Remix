package dev.ryanhcode.remix.forge;

import dev.ryanhcode.remix.Remix;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Remix.MOD_ID)
public class RemixForge {
    public RemixForge() {
        Remix.init();
    }
}
