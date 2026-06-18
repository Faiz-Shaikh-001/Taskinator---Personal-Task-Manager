package JsonParser;
import java.util.*;

public class JsonParser {
    public static final String JsonSerializer = null;
    private final List<Token> tokens;
    private int ptr = 0;

    public JsonParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek() {
        return tokens.get(ptr);
    }

    private Token consume(TokenType type) {
        Token t = peek();
        if (t.type != type)
            throw new RuntimeException("Expected token " + type + " but got " + t.type);
        ptr++;
        return t;
    }

    public Map<String, Object> parseMap() {
        Map<String, Object> map = new HashMap<>();
        consume(TokenType.BEGIN_OBJ);

        while (peek().type != TokenType.END_OBJ) {
            Token keyToken = consume(TokenType.STRING);
            consume(TokenType.NAME_SEP);

            if (peek().type == TokenType.BEGIN_OBJ) {
                map.put(keyToken.value, parseMap());
            } else if (peek().type == TokenType.STRING) {
                map.put(keyToken.value, consume(TokenType.STRING));
            } else {
                throw new RuntimeException("Unexpected token: " + peek());
            }

            if (peek().type == TokenType.VALUE_SEP) {
                consume(TokenType.VALUE_SEP);
            } else if (peek().type != TokenType.END_OBJ) {
                throw new RuntimeException("Expected comma or closing brace");
            }
        }
        consume(TokenType.END_OBJ);
        return map;
    }

    public static void main(String[] args) {
        String json = "{\"title\": \"Java Parser\", \"status\": \"Working\"}";

        JsonLexer lexer = new JsonLexer(json);
        List<Token> tokens = lexer.tokenize();
        JsonParser parser = new JsonParser(tokens);
        Map<String, Object> result = parser.parseMap();

        System.out.println("Parsed Map output: " + result);
    }
}