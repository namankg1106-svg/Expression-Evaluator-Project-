import java.util.*;

public class Main {

    // Stores calculation history
    static ArrayList<String> history = new ArrayList<>();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("======================================");
        System.out.println("       JAVA EXPRESSION EVALUATOR");
        System.out.println("======================================");
        System.out.println("Operators: +  -  *  /  %  ^");
        System.out.println("Parentheses: ( )");
        System.out.println("Type 'history' to see history");
        System.out.println("Type 'exit' to close");
        System.out.println("======================================");

        while (true) {

            System.out.print("\nEnter expression: ");
            String expression = sc.nextLine().trim();

            if (expression.equalsIgnoreCase("exit")) {
                System.out.println("Program closed.");
                break;
            }

            if (expression.equalsIgnoreCase("history")) {
                showHistory();
                continue;
            }

            if (expression.isEmpty()) {
                System.out.println("Expression cannot be empty.");
                continue;
            }

            try {

                double result = evaluate(expression);

                System.out.println("Result: " + formatResult(result));

            } catch (Exception e) {

                System.out.println("Error: " + e.getMessage());
            }
        }

        sc.close();
    }

    // ============================================
    // MAIN EVALUATION METHOD
    // ============================================

    static double evaluate(String expression) {

        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        int i = 0;

        while (i < expression.length()) {

            char ch = expression.charAt(i);

            // Ignore spaces
            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }

            // ====================================
            // NUMBER
            // ====================================

            if (Character.isDigit(ch) || ch == '.') {

                StringBuilder number = new StringBuilder();
                boolean decimalFound = false;

                while (i < expression.length()) {

                    char current = expression.charAt(i);

                    if (Character.isDigit(current)) {

                        number.append(current);
                        i++;

                    } else if (current == '.') {

                        if (decimalFound) {
                            throw new IllegalArgumentException(
                                    "Invalid number"
                            );
                        }

                        decimalFound = true;
                        number.append(current);
                        i++;

                    } else {
                        break;
                    }
                }

                numbers.push(
                        Double.parseDouble(number.toString())
                );

                continue;
            }

            // ====================================
            // OPENING PARENTHESIS
            // ====================================

            if (ch == '(') {

                operators.push(ch);
                i++;

                continue;
            }

            // ====================================
            // CLOSING PARENTHESIS
            // ====================================

            if (ch == ')') {

                while (!operators.isEmpty()
                        && operators.peek() != '(') {

                    applyOperator(numbers, operators);
                }

                if (operators.isEmpty()) {

                    throw new IllegalArgumentException(
                            "Missing opening parenthesis"
                    );
                }

                // Remove '('
                operators.pop();

                i++;

                continue;
            }

            // ====================================
            // OPERATOR
            // ====================================

            if (isOperator(ch)) {

                // Handle negative numbers
                if (ch == '-'
                        && (i == 0
                        || expression.charAt(i - 1) == '('
                        || isOperator(expression.charAt(i - 1)))) {

                    int next = i + 1;

                    // Skip spaces3
                    while (next < expression.length()
                            && Character.isWhitespace(
                            expression.charAt(next))) {

                        next++;
                    }

                    // Negative number
                    if (next < expression.length()
                            && (Character.isDigit(
                            expression.charAt(next))
                            || expression.charAt(next) == '.')) {

                        StringBuilder number =
                                new StringBuilder("-");

                        next++;

                        boolean decimalFound = false;

                        while (next < expression.length()) {

                            char current =
                                    expression.charAt(next);

                            if (Character.isDigit(current)) {

                                number.append(current);
                                next++;

                            } else if (current == '.') {

                                if (decimalFound) {
                                    throw new IllegalArgumentException(
                                            "Invalid number"
                                    );
                                }

                                decimalFound = true;
                                number.append(current);
                                next++;

                            } else {
                                break;
                            }
                        }

                        numbers.push(
                                Double.parseDouble(
                                        number.toString()
                                )
                        );

                        i = next;

                        continue;
                    }
                }

                // Apply previous operators based
                // on precedence
                while (!operators.isEmpty()
                        && operators.peek() != '('
                        && shouldApply(
                        operators.peek(), ch)) {

                    applyOperator(numbers, operators);
                }

                operators.push(ch);

                i++;

                continue;
            }

            // ====================================
            // INVALID CHARACTER
            // ====================================

            throw new IllegalArgumentException(
                    "Invalid character: " + ch
            );
        }

        // Apply remaining operators
        while (!operators.isEmpty()) {

            if (operators.peek() == '(') {

                throw new IllegalArgumentException(
                        "Missing closing parenthesis"
                );
            }

            applyOperator(numbers, operators);
        }

        if (numbers.size() != 1) {

            throw new IllegalArgumentException(
                    "Invalid expression"
            );
        }

        double result = numbers.pop();

        history.add(
                expression + " = " + formatResult(result)
        );

        return result;
    }

    // ============================================
    // APPLY OPERATOR
    // ============================================

    static void applyOperator(
            Stack<Double> numbers,
            Stack<Character> operators) {

        if (numbers.size() < 2) {

            throw new IllegalArgumentException(
                    "Invalid expression"
            );
        }

        char operator = operators.pop();

        double second = numbers.pop();
        double first = numbers.pop();

        double result;

        switch (operator) {

            case '+':

                result = first + second;
                break;

            case '-':

                result = first - second;
                break;

            case '*':

                result = first * second;
                break;

            case '/':

                if (second == 0) {

                    throw new ArithmeticException(
                            "Cannot divide by zero"
                    );
                }

                result = first / second;
                break;

            case '%':

                if (second == 0) {

                    throw new ArithmeticException(
                            "Cannot perform modulo by zero"
                    );
                }

                result = first % second;
                break;

            case '^':

                result = Math.pow(first, second);
                break;

            default:

                throw new IllegalArgumentException(
                        "Unknown operator"
                );
        }

        numbers.push(result);
    }

    // ============================================
    // CHECK OPERATOR
    // ============================================

    static boolean isOperator(char ch) {

        return ch == '+'
                || ch == '-'
                || ch == '*'
                || ch == '/'
                || ch == '%'
                || ch == '^';
    }

    // ============================================
    // OPERATOR PRECEDENCE
    // ============================================

    static int precedence(char operator) {

        switch (operator) {

            case '^':
                return 3;

            case '*':
            case '/':
            case '%':
                return 2;

            case '+':
            case '-':
                return 1;

            default:
                return 0;
        }
    }

    // ============================================
    // CHECK WHICH OPERATOR TO APPLY FIRST
    // ============================================

    static boolean shouldApply(
            char topOperator,
            char currentOperator) {

        int top = precedence(topOperator);
        int current = precedence(currentOperator);

        if (top > current) {
            return true;
        }

        if (top < current) {
            return false;
        }

        // ^ is right associative
        if (currentOperator == '^') {
            return false;
        }

        return true;
    }

    // ============================================
    // HISTORY
    // ============================================

    static void showHistory() {

        if (history.isEmpty()) {

            System.out.println(
                    "No calculation history."
            );

            return;
        }

        System.out.println(
                "\n========== HISTORY =========="
        );

        for (int i = 0; i < history.size(); i++) {

            System.out.println(
                    (i + 1) + ". " + history.get(i)
            );
        }

        System.out.println(
                "============================="
        );
    }

    // ============================================
    // FORMAT RESULT
    // ============================================

    static String formatResult(double result) {

        if (result == (long) result) {

            return String.valueOf((long) result);
        }

        return String.valueOf(result);
    }
}