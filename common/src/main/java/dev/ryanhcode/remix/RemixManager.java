package dev.ryanhcode.remix;

import dev.ryanhcode.remix.lang.RemixEntry;
import dev.ryanhcode.remix.lang.RemixLexer;
import dev.ryanhcode.remix.lang.RemixParser;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Internal
public final class RemixManager {

    private static final Map<String, ClassRemix> REMIXES = new HashMap<>();

    private RemixManager() {
    }

    public static void add(InputStream data) throws IOException {
        String input = IOUtils.toString(data, StandardCharsets.UTF_8);

        try {
            for (RemixEntry entry : RemixParser.parse(RemixLexer.lex(input))) {
                ClassRemix remix = REMIXES.computeIfAbsent(entry.classTarget(), ClassRemix::new);
                remix.add(entry);
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static byte @Nullable [] transform(String className, Function<String, ClassReader> classSource) {
        ClassRemix remix = REMIXES.get(className);
        if (remix == null) {
            return null;
        }

        ClassReader classReader = classSource.apply(className);
        ClassNode node = new ClassNode();
        classReader.accept(node, 0);

        remix.apply(node);

        ClassWriter cw = new ClassWriter(Opcodes.ASM9);
        node.accept(cw);
        return cw.toByteArray();
    }
}
