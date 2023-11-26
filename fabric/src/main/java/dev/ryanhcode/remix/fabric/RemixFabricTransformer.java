package dev.ryanhcode.remix.fabric;

import dev.ryanhcode.remix.Remix;
import dev.ryanhcode.remix.RemixManager;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.game.patch.GameTransformer;
import net.fabricmc.loader.impl.util.ExceptionUtil;
import net.fabricmc.loader.impl.util.LoaderUtil;
import net.fabricmc.loader.impl.util.SimpleClassPath;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.ClassReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipError;

@ApiStatus.Internal
public class RemixFabricTransformer extends GameTransformer {

    private final GameTransformer delegate;
    private final SimpleClassPath classPath;
    private final Map<String, byte[]> remixedClasses;

    public RemixFabricTransformer(GameTransformer delegate, List<Path> gameJars) {
        this.delegate = delegate;
        this.classPath = new SimpleClassPath(gameJars);
        this.remixedClasses = new HashMap<>();

        for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
            container.findPath("META-INF/remix").ifPresent(folder -> {
                try (Stream<Path> walk = Files.walk(folder)) {
                    walk.forEach(path -> {
                        if (!Files.isRegularFile(path)) {
                            return;
                        }
                        if (!path.getFileName().endsWith(".remix")) {
                            Remix.LOGGER.warn("Non-remix file {} was in META-INF/remix folder for mod: {}", path.getFileName(), container.getMetadata().getId());
                            return;
                        }

                        try (BufferedInputStream stream = new BufferedInputStream(Files.newInputStream(path))) {
                            RemixManager.add(stream);
                        } catch (Exception e) {
                            Remix.LOGGER.error("Failed to load remix '{}' from mod: {}", path.getFileName(), container.getMetadata().getId(), e);
                        }
                    });
                } catch (Exception e) {
                    Remix.LOGGER.error("Failed to find remixes for mod: {}", container.getMetadata().getId(), e);
                }
            });
        }
    }

    private ClassReader getClassSource(String name) {
        byte[] data = this.remixedClasses.get(name);

        if (data != null) {
            return new ClassReader(data);
        }

        try {
            SimpleClassPath.CpEntry entry = this.classPath.getEntry(LoaderUtil.getClassFileName(name));

            if (entry == null) {
                return null;
            }

            try (InputStream is = entry.getInputStream()) {
                return new ClassReader(is);
            } catch (IOException | ZipError e) {
                throw new RuntimeException(String.format("error reading %s in %s: %s", name, LoaderUtil.normalizePath(entry.getOrigin()), e), e);
            }
        } catch (IOException e) {
            throw ExceptionUtil.wrap(e);
        }
    }

    @Override
    public byte[] transform(String className) {
        byte[] transform = this.delegate.transform(className);
        if (transform != null) {
            return transform;
        }

        byte[] patched = this.remixedClasses.get(className);
        if (patched != null) {
            return patched;
        }

        byte[] remixed = RemixManager.transform(className, this::getClassSource);
        if (remixed != null) {
            this.remixedClasses.put(className, remixed);
            return remixed;
        }
        return null;
    }
}
