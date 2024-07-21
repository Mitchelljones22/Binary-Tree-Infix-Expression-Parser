import java.util.Stack;

public class InfixBinaryTree {
    public static void main(String[] args) throws Exception {
        String expression = new String("(5*2)-10/2*(7+3)");
        expression.replaceAll("\\s+", "");
        Node root = (BinaryTreeParse(expression));

        // This is just to show the tree layout for this one expression
        // This will be deleted later, I just wanted to test the structure of the tree
        System.out.println("   " + root.value);
        System.out.println("  "+root.left.value + " " +root.right.value);
        System.out.println(root.left.left.value + " " + root.left.right.value + " " +root.right.left.value +  " " + root.right.right.value);
        System.out.println("  " + root.right.left.left.value + " " + root.right.left.right.value + root.right.right.left.value + root.right.right.right.value);

    }

    public static Node BinaryTreeParse(String equation) throws Exception{
        Stack<Node> operatorStack = new Stack<>();
        Stack<Node> numberStack = new Stack<>();

        StringBuilder digitBuilder = new StringBuilder();

        for (int i = 0; i < equation.length(); i++) {
            char currentCharacter = equation.charAt(i);
            if (Character.isDigit(currentCharacter)){ // Checks for digit
                digitBuilder.append(currentCharacter);// Builds multi-digit operand
            } else{// Is not a digit
                if (digitBuilder.length() > 0) { // Checks if an operand is in the string builder
                    int nodeNumber = Integer.parseInt(digitBuilder.toString()); // Converts the multi-digit String into an int
                    numberStack.push(new Node(nodeNumber)); // Convers the nodeNumber into a node, and pushes to the stack
                    digitBuilder.setLength(0); // Clears the StringBuilder
                }
                if (currentCharacter == '(') {
                    operatorStack.push(new Node("(")); // Add "(" to operand stack
                } else if (isOperator(currentCharacter)) { // Is Operator
                    String operatorStr = String.valueOf(currentCharacter); // Used to parse multi-character operators
                    if (i + 1 < equation.length() && Node.isOperatorPair(currentCharacter, equation.charAt(i + 1))) { // Check for multi-character operators
                        operatorStr += equation.charAt(i + 1);
                        i++; // Skips next character 
                    }

                    Node newOperator = new Node(operatorStr); // Creates new operator from operator string
                    while (!operatorStack.isEmpty() && operatorStack.peek().precedence >= newOperator.precedence) { // If stack is not empty and current presedence is <= last operand in stack
                        evaluateOperation(operatorStack, numberStack); // Evaluate previous operation in stack
                    }
                    operatorStack.push(newOperator);// Push current operator to stack
                } else if (currentCharacter == ')') { // Evaluate Parenthesis
                    while (!operatorStack.peek().value.equals("(")) { // Ends when finds "("
                        evaluateOperation(operatorStack, numberStack);
                    }
                    operatorStack.pop(); // Remove the opening "(" from the stack
                
                }
            }

        }
        // Push any remaining operands into stack
        if (digitBuilder.length() > 0) {
            int nodeNumber = Integer.parseInt(digitBuilder.toString());
            numberStack.push(new Node(nodeNumber));
        }

        // Final evaluation of the remaining operations in the stacks
        while (!operatorStack.isEmpty()) {
            if (numberStack.size() < 2) { 
                throw new Exception("Invalid expression: not enough operands");
            }
            evaluateOperation(operatorStack, numberStack);
        }

        // The root node should be the only node left in the operatorStack
        // Combines all remaining subtrees by subtree root
        if (operatorStack.isEmpty()) {
            if (numberStack.size() == 1) {
                return numberStack.pop(); // Case of a single number expression
            } else {
                throw new Exception("Invalid expression: no operators");
            }
        }
        return operatorStack.pop(); // Return the root of the entire tree
    }
    public static boolean isOperator(char testCharacter) {
        if (testCharacter == '+' || testCharacter == '-' || testCharacter == '*' || 
            testCharacter == '/' || testCharacter == '%' || testCharacter == '^' ||
            testCharacter == '&' || testCharacter == '|' || testCharacter == '<' || 
            testCharacter == '>' || testCharacter == '=' || testCharacter == '!') {
            return true;
        }
        return false;
    }

    /** Evaluates top operation on the stack
        @param operatorStack: Stack of Operators
        @param numberStack: Stack of Operands
    */
    public static void evaluateOperation(Stack<Node> operatorStack, Stack<Node> numberStack) {
        Node rightOperand = numberStack.pop();
        Node leftOperand = numberStack.pop();
        Node node = operatorStack.pop();
        Node subtreeRoot = node.createSubtree(leftOperand, rightOperand);
        numberStack.push(subtreeRoot);
    }
    
}
