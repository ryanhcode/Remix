package dev.ryanhcode.remix.fabric;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider;
import net.fabricmc.loader.impl.game.patch.GameTransformer;
import org.jetbrains.annotations.ApiStatus;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;

@ApiStatus.Internal
public class RemixFabricPreLaunch implements PreLaunchEntrypoint {

    @SuppressWarnings("unchecked")
    @Override
    public void onPreLaunch() {
        try {
            MinecraftGameProvider gameProvider = (MinecraftGameProvider) FabricLoaderImpl.INSTANCE.getGameProvider();
            GameTransformer transformer = gameProvider.getEntrypointTransformer();

            Field gameJarsField = MinecraftGameProvider.class.getDeclaredField("gameJars");
            gameJarsField.setAccessible(true);
            List<Path> gameJars = (List<Path>) gameJarsField.get(gameProvider);

            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            Field transformerField = MinecraftGameProvider.class.getDeclaredField("transformer");
            unsafe.putObject(gameProvider, unsafe.objectFieldOffset(transformerField), new RemixFabricTransformer(transformer, gameJars));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}