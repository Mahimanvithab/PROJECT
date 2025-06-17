import java.util.Scanner;
import java.text.DecimalFormat;

public class LinkedListCalculator {

    // Formatter to show up to 10 decimal places
    private static final DecimalFormat df = new DecimalFormat("#.##########");

    // Node class for linked list
    private static class Node {
        String data;
        Node next;

        Node(String data) {
            this.data = data;
            this.next = null;
        }
    }

    // Custom LinkedList for expression
    private static class ExpressionList {
        private Node head, tail;
        private int size = 0;

        public void add(String data) {
            Node n = new Node(data);
            if (head == null) {
                head = tail = n;
            } else {
                tail.next = n;
                tail = n;
            }
            size++;
        }

        public void addFirst(String data) {
            Node n = new Node(data);
            if (head == null) {
                head = tail = n;
            } else {
                n.next = head;
                head = n;
            }
            size++;
        }

        public String get(int index) {
            if (index < 0 || index >= size) return null;
            Node c = head;
            for (int i = 0; i < index; i++) c = c.next;
            return c.data;
        }

        public String getLast() {
            return tail != null ? tail.data : null;
        }

        public int size() {
            return size;
        }

        public void clear() {
            head = tail = null;
            size = 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            Node c = head;
            while (c != null) {
                sb.append(c.data);
                c = c.next;
            }
            return sb.toString();
        }

        public void addAll(ExpressionList other) {
            Node c = other.head;
            while (c != null) {
                this.add(c.data);
                c = c.next;
            }
        }

        public boolean isEmpty() {
            return size == 0;
        }
    }

    // Wrapper to return result + expression list from parenthesis
    private static class ParenthesisResult {
        double result;
        ExpressionList expression;

        ParenthesisResult(double result, ExpressionList expression) {
            this.result = result;
            this.expression = expression;
        }
    }

    // Main calculator logic
    public static void runCalculator(Scanner sc) {
        System.out.println("\n--- Running Calculator with custom LinkedList ---");

        double result = getFirstDigit(sc);
        ExpressionList expression = new ExpressionList();
        expression.add(formatNumber(result));

        mainLoop:
        while (true) {
            printMainMenu();
            int choice = getIntInput(sc, 1, 11);

            switch (choice) {
                case 1: // Addition
                    double addNum = getNextDigit(sc);
                    result += addNum;
                    expression.add(" + ");
                    expression.add(formatNumber(addNum));
                    break;

                case 2: // Subtraction
                    double subNum = getNextDigit(sc);
                    result -= subNum;
                    expression.add(" - ");
                    expression.add(formatNumber(subNum));
                    break;

                case 3: // Multiplication
                    double mulNum = getNextDigit(sc);
                    result *= mulNum;
                    expression.add(" * ");
                    expression.add(formatNumber(mulNum));
                    break;

                case 4: // Division
                    double divNum = getNextDigit(sc);
                    if (divNum == 0) {
                        System.out.println("Error: Division by zero");
                        break;
                    }
                    result /= divNum;
                    expression.add(" / ");
                    expression.add(formatNumber(divNum));
                    break;

                case 5: // Result and reset
                    System.out.println("Expression: " + expression.toString());
                    System.out.println("Result: " + formatNumber(result));
                    return;

                case 6: // Return to menu
                    break mainLoop;

                case 7: // Handle parenthesis
                    ParenthesisResult pResult = handleParenthesis(sc);
                    if (!pResult.expression.isEmpty()) {
                        int op = chooseParenthesisOperator(sc);
                        if (op == 9) break;

                        switch (op) {
                            case 1: result += pResult.result; expression.add(" + "); break;
                            case 2: result -= pResult.result; expression.add(" - "); break;
                            case 3: result *= pResult.result; expression.add(" * "); break;
                            case 4:
                                if (pResult.result == 0) {
                                    System.out.println("Error: Division by zero");
                                    continue;
                                }
                                result /= pResult.result;
                                expression.add(" / ");
                                break;
                            case 5: result = Math.pow(result, pResult.result); expression.add(" ^ "); break;
                            case 6: result %= pResult.result; expression.add(" % "); break;
                            case 7: result = Math.sqrt(result); expression.add(" √ "); break;
                            case 8:
                                if (result < 0 || result != (int) result) {
                                    System.out.println("Error: Invalid factorial");
                                    continue;
                                }
                                result = factorial((int) result);
                                expression.add("!");
                                break;
                        }
                        expression.add("(");
                        expression.addAll(pResult.expression);
                        expression.add(")");
                    }
                    break;

                case 8: // Square root
                    if (result < 0) {
                        System.out.println("Error: Square root of negative number");
                        break;
                    }
                    result = Math.sqrt(result);
                    expression.add(" √ ");
                    break;

                case 9: // Power
                    double power = getNextDigit(sc);
                    result = Math.pow(result, power);
                    expression.add(" ^ ");
                    expression.add(formatNumber(power));
                    break;

                case 10: // Modulo
                    double mod = getNextDigit(sc);
                    result %= mod;
                    expression.add(" % ");
                    expression.add(formatNumber(mod));
                    break;

                case 11: // Factorial
                    if (result < 0 || result != (int) result) {
                        System.out.println("Error: Invalid factorial");
                        break;
                    }
                    result = factorial((int) result);
                    expression.add("!");
                    break;
            }
        }
    }

    // Print the calculator menu
    private static void printMainMenu() {
        System.out.println("\n--- Calculator Menu (LinkedList) ---");
        System.out.println("1.Add(+) 2.Sub(-) 3.Mul(*) 4.Div(/) 5.Result(=)");
        System.out.println("6.Return to Main Menu 7.Parenthesis() 8.Sqrt(√) 9.Power(^) 10.Mod(%) 11.Factorial(!)");
        System.out.print("Choose an operation (1-11): ");
    }

    // Choose operator to apply to parentheses result
    private static int chooseParenthesisOperator(Scanner sc) {
        System.out.println("Operator for parenthesis: 1.+ 2.- 3.* 4./ 5.^ 6.% 7.√ 8.! 9.Cancel");
        return getIntInput(sc, 1, 9);
    }

    // Handle parenthesis operations and return result + expression
    private static ParenthesisResult handleParenthesis(Scanner sc) {
        double result = getFirstDigit(sc);
        ExpressionList expr = new ExpressionList();
        expr.add(formatNumber(result));

        while (true) {
            System.out.println("\n--()Menu--\n1.+ 2.- 3.* 4./ 5.= 6.Cancel 7.√ 8.^ 9.% 10.!");
            int choice = getIntInput(sc, 1, 10);
            if (choice == 5) return new ParenthesisResult(result, expr);
            if (choice == 6) return new ParenthesisResult(0, new ExpressionList());

            switch (choice) {
                case 1: double a = getNextDigit(sc); result += a; expr.add(" + "); expr.add(formatNumber(a)); break;
                case 2: double b = getNextDigit(sc); result -= b; expr.add(" - "); expr.add(formatNumber(b)); break;
                case 3: double c = getNextDigit(sc); result *= c; expr.add(" * "); expr.add(formatNumber(c)); break;
                case 4: double d = getNextDigit(sc);
                        if (d == 0) { System.out.println("Error: Division by zero"); break; }
                        result /= d; expr.add(" / "); expr.add(formatNumber(d)); break;
                case 7:
                    if (result < 0) {
                        System.out.println("Error: Square root of negative number");
                        break;
                    }
                    result = Math.sqrt(result); expr.add(" √ "); break;
                case 8: double p = getNextDigit(sc); result = Math.pow(result, p); expr.add(" ^ "); expr.add(formatNumber(p)); break;
                case 9: double m = getNextDigit(sc); result %= m; expr.add(" % "); expr.add(formatNumber(m)); break;
                case 10:
                    if (result < 0 || result != (int) result) {
                        System.out.println("Error: Invalid factorial");
                        break;
                    }
                    result = factorial((int) result); expr.add("!"); break;
            }
        }
    }

    // Get first digit input
    private static double getFirstDigit(Scanner sc) {
        System.out.print("Enter the first digit: ");
        while (!sc.hasNextDouble()) {
            System.out.print("Enter a number: ");
            sc.next();
        }
        double val = sc.nextDouble(); sc.nextLine();
        return val;
    }

    // Get next digit input
    private static double getNextDigit(Scanner sc) {
        System.out.print("Enter the next digit: ");
        while (!sc.hasNextDouble()) {
            System.out.print("Enter a number: ");
            sc.next();
        }
        double val = sc.nextDouble(); sc.nextLine();
        return val;
    }

    // Get integer input with range validation
    private static int getIntInput(Scanner sc, int min, int max) {
        while (true) {
            String line = sc.nextLine();
            try {
                int val = Integer.parseInt(line.trim());
                if (val >= min && val <= max) return val;
            } catch (NumberFormatException e) {}
            System.out.print("Choose option (" + min + "-" + max + "): ");
        }
    }

    // Calculate factorial
    private static long factorial(int n) {
        if (n == 0 || n == 1) return 1;
        long res = 1;
        for (int i = 2; i <= n; i++) res *= i;
        return res;
    }

    // Format numbers (e.g., show 3 instead of 3.0)
    private static String formatNumber(double num) {
        if (num == (long) num) {
            return String.valueOf((long) num);
        } else {
            return df.format(num);
        }
    }

    // Optional: main method to run
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        runCalculator(scanner);
        scanner.close();
    }
}