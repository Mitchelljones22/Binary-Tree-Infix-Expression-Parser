// Binary Tree Infix Expression Parser
// Developed by: Mitchell Jones
// Last Updated: 07/23/24


import java.util.Stack;
import java.io.File;
import java.util.Scanner;

public class InfixBinaryTree {
    public static void main(String[] args) throws Exception {

        File expresisonFile = new File("TeamProject3/src/expressions.txt");
        Scanner fileScanner = new Scanner(expresisonFile);

        while (fileScanner.hasNextLine()){ // Loops for evey line in expressions.txt
            String expression = (fileScanner.nextLine()); // Expression = contents of line
            System.out.println("-".repeat(30));
            System.out.println("Expression: " + expression);
            try {
                Node expressionTreeRoot = BinaryTreeParse(expression); // Expression is parsed into a binary tree
                System.out.println("Root Node: " + expressionTreeRoot.value); // Root Node of the binary tree
                String answer = inorderEvaluation(expressionTreeRoot); // Evaluates the binary expression tree // This could be added to the end of BinaryTreeParse()
                System.out.println("Solution: " + answer); // Solution                                         // But I wanted to display the root node as well.
            } catch (IllegalArgumentException e) { // Divide by zero error
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("-".repeat(30));
        fileScanner.close();
    }

    /** Parses an infix expression, builds a binary tree, and solves using inorder traversal
        @param equation: expression to be parsed
        @return: Root of parsed binary expression tree
    */
    public static Node BinaryTreeParse(String equation) throws Exception{
        Stack<Node> operatorStack = new Stack<>(); // Stores Operator nodes, and subtrees
        Stack<Node> numberStack = new Stack<>(); // Stores operands as nodes

        StringBuilder digitBuilder = new StringBuilder(); // Builds multi-digit Nodes

        for (int i = 0; i < equation.length(); i++) {
            char currentCharacter = equation.charAt(i);
            if (Character.isDigit(currentCharacter)){ // Checks for digit
                digitBuilder.append(currentCharacter);// Builds multi-digit operand
            } else{// Is not a digit
                if (digitBuilder.length() > 0) { // Checks if an operand is in the string builder
                    int nodeNumber = Integer.parseInt(digitBuilder.toString()); // Converts the multi-digit String into an int
                    numberStack.push(new Node(nodeNumber)); // Converts the nodeNumber into a node, and pushes to the stack
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

                    Node newOperator = new Node(operatorStr); // Creates new operator node from operator string
                    while (!operatorStack.isEmpty() && operatorStack.peek().precedence >= newOperator.precedence) { // If stack is not empty and current presedence is <= last node in stack
                        evaluateOperation(operatorStack, numberStack); // Creates a subtree using the previous operation in the stack
                    }
                    operatorStack.push(newOperator);// Push current operator node to stack
                } else if (currentCharacter == ')') { // Evaluate Parenthesis
                    while (!operatorStack.peek().value.equals("(")) { // Ends when finds "("
                        evaluateOperation(operatorStack, numberStack);
                    }
                    operatorStack.pop(); // Remove the opening "(" from the stack
                
                }
            }

        }
        // Push any remaining operand nodes into stack
        if (digitBuilder.length() > 0) {
            int nodeNumber = Integer.parseInt(digitBuilder.toString());
            numberStack.push(new Node(nodeNumber));
        }

        // Final evaluation of the remaining operations in the stacks
        while (!operatorStack.isEmpty()) {
            if (numberStack.size() < 2) { 
                throw new Exception("Invalid expression: not enough operands");
            }
            evaluateOperation(operatorStack, numberStack); // Creates subtree
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

    /** Checks if current character is an operator 
        @param testCharacter: Current character being tested
        @return: True if operator symbol, false if not
    */
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
        @param operatorStack: Stack of Operator Nodes
        @param numberStack: Stack of Operand Nodes
        @return Root of the new subtree
    */
    public static void evaluateOperation(Stack<Node> operatorStack, Stack<Node> numberStack) {
        Node rightOperand = numberStack.pop();
        Node leftOperand = numberStack.pop();
        Node node = operatorStack.pop();
        Node subtreeRoot = node.createSubtree(leftOperand, rightOperand);
        numberStack.push(subtreeRoot);
    }

     /** Uses Inorder Traversal to Evaluate the Binary Tree
        @param root: Root note of the binary tree to be evaluated
        @throws Exception : Divide by zero error
        Time Complexity: O(n)
    */
    public static String inorderEvaluation(Node root) throws Exception{
        try{            
            if (root == null) { // Node doesnt exists
                return "0"; // Return to parent node
            }
            int leftOperand = Integer.parseInt(inorderEvaluation(root.left)); // Recursivly Evaluate the left node
            
            String operatorSymbol = "";  // Declare OperatorSymbol
            
            if(root.isOperator){ // Checking root node
                operatorSymbol = root.value;  // Assign value to OperatorSymbol
            } else {
                return (root.value);  // Return value if it's an operand
            }
            int rightOperand = Integer.parseInt(inorderEvaluation(root.right)); // Recursivly Evaluate the right node
            
            return String.valueOf(Evaluate(leftOperand, operatorSymbol, rightOperand));  // Evaluate operation using root operator and its children

        // Divide by zero handling 
        } catch (IllegalArgumentException e) { // inorderEvaluation used to return answer as a int, 
            return e.getMessage();             // but was changed to string to handle error messages
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }


    /** Evaluates subtree
        @param leftOperand: left child of root/operator
        @param operator: operator node/root of operand nodes
        @param rightOperand: right child of root/operator
    */
    public static int Evaluate(int leftOperand, String operator, int rightOperand) throws Exception {
        return switch (operator) {
            case "+" -> leftOperand + rightOperand;
            case "-" -> leftOperand - rightOperand;
            case "*" -> leftOperand * rightOperand;
            case "/" -> {
                if (rightOperand != 0) { // Checks for divide by zero error
                    yield leftOperand / rightOperand;
                } else {
                    throw new IllegalArgumentException("Divide by zero error");
                }
            }
            case "^" -> (int) Math.pow(leftOperand, rightOperand);
            case "%" -> leftOperand % rightOperand;
            case "&&" -> (leftOperand != 0 && rightOperand != 0) ? 1 : 0;
            case "||" -> (leftOperand != 0 || rightOperand != 0) ? 1 : 0;
            case "<" -> leftOperand < rightOperand ? 1 : 0;
            case "<=" -> leftOperand <= rightOperand ? 1 : 0;
            case ">" -> leftOperand > rightOperand ? 1 : 0;
            case ">=" -> leftOperand >= rightOperand ? 1 : 0;
            case "==" -> leftOperand == rightOperand ? 1 : 0;
            case "!=" -> leftOperand != rightOperand ? 1 : 0;
            default -> throw new IllegalArgumentException("Invalid operator: " + operator); // Operator does not exist
        };
    }
}
