import java.util.NoSuchElementException;

public class Node {
    String value;
    int precedence;
    boolean isOperator;
    Node left, right;

    // Constructor for numbers
    public Node(int number) {
        this.value = String.valueOf(number);
        this.isOperator = false;
        this.precedence = 0;  // Numbers have lowest precedence
        this.left = null;
        this.right = null;
    }

    // Constructor for operators
    public Node(String symbol) {
        this.value = symbol;
        this.isOperator = true;
        this.left = null;
        this.right = null;

        this.precedence = switch (symbol) {
            case "^" -> 7;
            case "*", "/", "%" -> 6;
            case "+", "-" -> 5;
            case "<", "<=", ">", ">=" -> 4;
            case "==", "!=" -> 3;
            case "&&" -> 2;
            case "||" -> 1;
            case "(" -> -1;
            default -> throw new NoSuchElementException("Invalid operator: " + symbol);
        };
    }

    public static boolean isOperatorPair(char firstOp, char secondOp) {
        String pair = "" + firstOp + secondOp;
        return pair.equals("&&") || pair.equals("||") || pair.equals(">=") || pair.equals("<=") ||
               pair.equals("==") || pair.equals("!=");
    }

    public int getIntValue() {
        if (isOperator) {
            throw new IllegalStateException("Cannot get int value of an operator node");
        }
        return Integer.parseInt(value);
    }

    public Node createSubtree(Node leftOperand, Node rightOperand) {
        this.left = leftOperand;
        this.right = rightOperand;
        return this;

    }
}