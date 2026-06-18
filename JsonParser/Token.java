package JsonParser;

public class Token {
    final TokenType type;
    final String value;

    Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
