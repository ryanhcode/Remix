package dev.ryanhcode.remix;

import dev.ryanhcode.remix.lang.RemixEntry;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashSet;
import java.util.Set;

public class ClassRemix {

    private final String target;
    private final Set<RemixEntry> entries;

    public ClassRemix(String target) {
        this.target = target;
        this.entries = new HashSet<>();
    }

    public void add(RemixEntry entry) {
        this.entries.add(entry);
    }

    public void apply(ClassNode node) {
        // TODO
    }
}
