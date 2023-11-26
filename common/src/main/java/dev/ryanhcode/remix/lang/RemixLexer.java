package dev.ryanhcode.remix.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenizes a Remix file
 */
public class RemixLexer {


    public enum TokenType {

        // KEYWORDS
        DEFINE("\\#define"),
        IN("in"),
        LOCAL("local"),
        PARAM("param"),
        RESULT("result"),


        // EVERYTHING ELSE
        IDENTIFIER ("[a-zA-Z_0-9][a-zA-Z0-9_\\/;()\\.]*"),
        EQUALS("\\="),
        LEFT_PAREN("\\["),
        RIGHT_PAREN("\\]"),
        OPEN_TYPE("\\<"),
        CLOSE_TYPE("\\>"),
        OPEN_ENTRY("\\{"),
        CLOSE_ENTRY("\\}"),
        COLON(":"),
        VAGUE("\\.\\.\\."),
        DOT("\\."),
        COMMA("\\,"),
        NIL("erm.. what the scallop guys");

        private final Pattern regex;

        TokenType(String regex) {
            this.regex = Pattern.compile(regex);
        }

        public Pattern getRegex() {
            return regex;
        }
    }

    public static class Token {
        public TokenType type;
        public String contents;
        public int line;
        public int startIndex, endIndex;

        public Token(TokenType type, String contents, int line, int startIndex, int endIndex) {
            this.type = type;
            this.contents = contents;
            this.line = line;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public String toString() {
            return type + "(\"" + contents + "\")";
        }
    }

    public static List<Token> lex(String input) {
        List<Token> tokens = new ArrayList<>();
        int line = 1;
        int index = 0;
        while (index < input.length()) {
            char c = input.charAt(index);
            if(c == ' ') {
                index ++;
                continue;
            }
            boolean matchFound = false;
            for (TokenType tokenType : TokenType.values()) {
                String substring = input.substring(index);
                Matcher matcher = tokenType.getRegex().matcher(substring);
                if (matcher.find() && matcher.start() == 0) {
                    matchFound = true;
                    int startIndex = index;
                    int endIndex = index + matcher.end();

                    String contents = input.substring(startIndex, endIndex);

                    tokens.add(new Token(tokenType, contents, line, startIndex, endIndex));
                    index = endIndex;
                    break;
                }
            }
            if (!matchFound) {
                if (c == '\n') {
                    line++;
                    index++;
                    continue;
                }
                index++;
                throw new IllegalArgumentException("Token not recognized. Line: " + line + " Index: " + index + " Char: " + c);
            }
        }
        return tokens;
    }
}
