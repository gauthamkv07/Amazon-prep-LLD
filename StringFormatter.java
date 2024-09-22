import java.util.List;

public class StringFormatter {

    public static String formatString(String template, List<String> replacements) throws IllegalArgumentException {
        StringBuilder result = new StringBuilder();
        int replacementIndex = 0;
        int length = template.length();

        for (int i = 0; i < length; i++) {
            char ch = template.charAt(i);

            if (ch == '{') {
                // Ensure valid placeholder syntax
                if (i + 1 < length && template.charAt(i + 1) == '}') {
                    if (replacementIndex >= replacements.size()) {
                        throw new IllegalArgumentException("Not enough replacements for placeholders.");
                    }
                    result.append(replacements.get(replacementIndex));
                    replacementIndex++;
                    i++; // Skip the closing '}'
                } else {
                    throw new IllegalArgumentException("Invalid placeholder syntax.");
                }
            } else {
                result.append(ch);
            }
        }

        if (replacementIndex < replacements.size()) {
            throw new IllegalArgumentException("Too many replacements provided.");
        }

        return result.toString();
    }

    public static void main(String[] args) {
        // Example usage
        String template = "Hello, { }, welcome to { }!";
        List<String> replacements = List.of("Alice", "Wonderland");

        try {
            String result = formatString(template, replacements);
            System.out.println(result); // Output: Hello, Alice, welcome to Wonderland!
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}
