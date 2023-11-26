package dev.ryanhcode.remix;

import dev.ryanhcode.remix.lang.RemixEntry;
import dev.ryanhcode.remix.lang.RemixLexer;
import dev.ryanhcode.remix.lang.RemixParser;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

public class TokenizeTest {

    @Test
    public void testTokenization() {

        String testProgram = "#define project Lcom/ryanhcode/landlord/Landlord;projectIfNeccesary(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;\n" +
            "\n" +
            "WirelessNetwork in[cock] distanceToEntitySADG[Entity instance] {\n" +
            "  transform_vector[e.entityLevel, e.entityPosition]\n" +
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
