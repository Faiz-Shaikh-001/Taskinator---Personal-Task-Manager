package JsonParser;

import java.util.ArrayList;
import java.util.List;

public class JsonLexer {
    private final String src;
    private int cursor = 0;

    public JsonLexer(String src) {
        this.src = src;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (cursor < src.length()) {
            char c = src.charAt(cursor);

            // Skip character if it's a whitespace
            if (Character.isWhitespace(c)) {
                cursor++;
                continue;
            }

            switch (c) {
                case '{' -> {
                    tokens.add(new Token(TokenType.BEGIN_OBJ, "{"));
                    cursor++;
                }
                case '}' -> {
                    tokens.add(new Token(TokenType.END_OBJ, "}"));
                    cursor++;
                }
                case ':' -> {
                    tokens.add(new Token(TokenType.NAME_SEP, ":"));
                    cursor++;
                }
                case ',' -> {
                    tokens.add(new Token(TokenType.VALUE_SEP, ","));
                    cursor++;
                }
                case '"' -> tokens.add(parseStringToken());
                default -> throw new RuntimeException("Unexpected Character: " + c);
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private Token parseStringToken() {
        cursor++;
        int start = cursor;
        while (cursor < src.length() && src.charAt(cursor) != '"') {
            cursor++;
        }
        String val = src.substring(start, cursor);
        cursor++;
        return new Token(TokenType.STRING, val);
    }

}
