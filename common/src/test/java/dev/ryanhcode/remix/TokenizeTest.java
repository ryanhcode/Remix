package dev.ryanhcode.remix;

import dev.ryanhcode.remix.lang.RemixEntry;
import dev.ryanhcode.remix.lang.RemixLexer;
import dev.ryanhcode.remix.lang.RemixParser;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

public class TokenizeTest {

    @Test
    public void testTokenization() throws Exception {

        String testProgram = "#define Level net.minecraft.world.Level\n" +
            "#define Entity net.minecraft.world.entity.Entity\n" +
            "#define BeltBlock com.simibubi.create.content.kinetics.belt.BeltBlock\n" +
            "#define updateEntityAfterFallOn Lcom/simibubi/create/content/kinetics/belt/BeltBlock;updateEntityAfterFallOn(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;)V\n" +
            "#define blockPosition Lnet/minecraft/world/entity/Entity;blockPosition()Lnet/minecraft/core/BlockPos;\n" +
            "\n" +
            "BeltBlock in[updateEntityAfterFallOn] blockPosition project_position {\n" +
            "  level = param[0] \n" +
            "\n" +
            "\n" +
            "  position = result\n" +
            "}";

        List<RemixLexer.Token> tokens = RemixLexer.lex(testProgram);

        for (RemixLexer.Token token : tokens) {
            System.out.println(token);
        }

        System.out.println("\nParsing...\n");

        Collection<RemixEntry> entries = RemixParser.parse(tokens);

        for (RemixEntry entry : entries) {
            System.out.println(entry);
        }
    }

}
