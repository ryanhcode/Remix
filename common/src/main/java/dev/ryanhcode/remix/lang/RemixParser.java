package dev.ryanhcode.remix.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.ryanhcode.remix.lang.RemixLexer.TokenType.*;

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
        List<RemixParam> params = new ArrayList<>();

        while (peek().type != CLOSE_ENTRY) {
            String name = consume(IDENTIFIER).contents;

            consume(EQUALS);

            RemixParam.ParamType type = RemixParam.ParamType.PARAM;

            if(peek().type == PARAM) {
                consume(PARAM);
            } else if (peek().type == LOCAL) {
                consume(LOCAL);
                type = RemixParam.ParamType.LOCAL;
            } else if (peek().type == RESULT) {
                consume(RESULT);
                type = RemixParam.ParamType.RESULT;
            } else if (peek().type == IDENTIFIER && peek().contents.equals("this.")) {
                consume(IDENTIFIER);
                consume(DOT);
                type = RemixParam.ParamType.FIELD;
            }

            if (type == RemixParam.ParamType.RESULT) {
                params.add(new RemixParam(type, 0));
            } else {
                consume(LEFT_PAREN);
                int index = Integer.parseInt(consume(IDENTIFIER).contents);
                consume(RIGHT_PAREN);
                params.add(new RemixParam(type, index));
            }
        }

        consume(CLOSE_ENTRY);

        return new RemixEntry(classTarget, methodTarget, invokeTarget, remixFunction, params);
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