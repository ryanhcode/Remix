package dev.ryanhcode.remix.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.ryanhcode.remix.lang.RemixLexer.TokenType.CLOSE_ENTRY;
import static dev.ryanhcode.remix.lang.RemixLexer.TokenType.IDENTIFIER;
import static dev.ryanhcode.remix.lang.RemixLexer.TokenType.IN;
import static dev.ryanhcode.remix.lang.RemixLexer.TokenType.LEFT_PAREN;
import static dev.ryanhcode.remix.lang.RemixLexer.TokenType.NIL;
import static dev.ryanhcode.remix.lang.RemixLexer.TokenType.OPEN_ENTRY;
import static dev.ryanhcode.remix.lang.RemixLexer.TokenType.RIGHT_PAREN;

/**
 * Parses a lexed token list into a collection of remix entries.
 */
public class RemixParser {
    private final List<RemixLexer.Token> tokens;
    private int index;

    /**
     * Mapper from identifier to descriptor
     */
    private Map<String, String> defines = new HashMap<>();

    private List<RemixEntry> entries = new ArrayList<>();

    public static Collection<RemixEntry> parse(List<RemixLexer.Token> tokens) throws Exception {
        RemixParser parser = new RemixParser(tokens);
        parser.program();
        return parser.entries;
    }

    public RemixParser(List<RemixLexer.Token> tokens) {
        this.tokens = tokens;
        index = 0;
    }


    private void program() throws Exception {
        // consume defines
        while (peek().type == RemixLexer.TokenType.DEFINE) {
            consume(RemixLexer.TokenType.DEFINE);
            String identifier = consume(IDENTIFIER).contents;
            String descriptor = consume(IDENTIFIER).contents;
            defines.put(identifier, descriptor);
        }

        // consume entries
        while (peek().type != NIL) {
            entries.add(entry());
        }
    }

    private RemixEntry entry() throws Exception {
        String classTarget = checkDefinitions(consume(IDENTIFIER).contents);

        consume(IN);
        consume(LEFT_PAREN);

        String methodTarget = checkDefinitions(consume(IDENTIFIER).contents);

        consume(RIGHT_PAREN);

        String invokeTarget = checkDefinitions(consume(IDENTIFIER).contents);

        String remixFunction = checkDefinitions(consume(IDENTIFIER).contents);

        // starting the param body
        consume(OPEN_ENTRY);

        // consume params
        List<String> params = new ArrayList<>();

        consume(CLOSE_ENTRY);

        return new RemixEntry(classTarget, methodTarget, invokeTarget, "");
    }

    private String checkDefinitions(String contents) {
        if (defines.containsKey(contents)) {
            return defines.get(contents);
        }
        return contents;
    }


    private RemixLexer.Token peek() {
        if (index >= tokens.size()) {
            return new RemixLexer.Token(NIL, "", 0, 0, 0);
        }
        return tokens.get(index);
    }


    private RemixLexer.Token consume(RemixLexer.TokenType tokenType) throws Exception {
        RemixLexer.Token token = peek();
        if (token.type != tokenType) {
            throw new Exception("Expected " + tokenType + " but found " + token.type +
                    " at line " + token.line);
        }
        index++;
        return token;
    }

    private void consume(String identifier) throws Exception {
        if (peek().type != IDENTIFIER) {
            throw new Exception("Expected \"" + identifier +  "\" but found " + peek().contents +
                    " at line " + peek().line);
        }
        index++;
    }

}