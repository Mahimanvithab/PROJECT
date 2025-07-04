import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.text.DecimalFormat;

public class QueueCalculator1 {
    private static final DecimalFormat df = new DecimalFormat("#.##########");

    private static class ExpressionQueue {
        private final Queue<String> queue = new LinkedList<>();

        void add(String data) { queue.add(data); }

        void addFirst(String data) {
            LinkedList<String> temp = new LinkedList<>(queue);
            temp.addFirst(data);
            queue.clear();
            queue.addAll(temp);
        }

        String get(int index) {
            if(index < 0 || index >= queue.size()) return null;
            int i = 0;
            for(String s : queue) {
                if(i == index) return s;
                i++;
            }
            return null;
        }

        String getLast() {
            if (queue.isEmpty()) return null;
            if (queue instanceof LinkedList) return ((LinkedList<String>) queue).getLast();
            else {
                String last = null;
                for(String s : queue) last = s;
                return last;
            }
        }

        int size() { return queue.size(); }

        void clear() { queue.clear(); }

        boolean isEmpty() { return queue.isEmpty(); }

        void addAll(ExpressionQueue other) { queue.addAll(other.queue); }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (String s : queue) sb.append(s);
            return sb.toString();
        }
    }

    private static class ParenthesisResult {
        double result;
        ExpressionQueue expression;
        ParenthesisResult(double result, ExpressionQueue expression) {
            this.result = result;
            this.expression = expression;
        }
    }

    public static void runCalculator(Scanner sc) {
        System.out.println("\n--- Running Calculator with Queue ---");
        double result = getFirstDigit(sc);
        ExpressionQueue expression = new ExpressionQueue();
        expression.add(formatNumber(result));

        mainLoop:
        while (true) {
            printMainMenu();
            int choice = getIntInput(sc, 1, 11);

            switch (choice) {
                case 1:
                    double addNum = getNextDigit(sc);
                    result += addNum;
                    expression.add(" + ");
                    expression.add(formatNumber(addNum));
                    break;
                case 2:
                    double subNum = getNextDigit(sc);
                    result -= subNum;
                    expression.add(" - ");
                    expression.add(formatNumber(subNum));
                    break;
                case 3:
                    double mulNum = getNextDigit(sc);
                    result *= mulNum;
                    expression.add(" * ");
                    expression.add(formatNumber(mulNum));
                    break;
                case 4:
                    double divNum = getNextDigit(sc);
                    if (divNum == 0) {
                        System.out.println("Cannot divide by zero.");
                    } else {
                        result /= divNum;
                        expression.add(" / ");
                        expression.add(formatNumber(divNum));
                    }
                    break;
                case 5:
                    System.out.println("Expression: " + expression.toString() + " = " + formatNumber(result));
                    result = getFirstDigit(sc);
                    expression.clear();
                    expression.add(formatNumber(result));
                    break;
                case 6:
                    System.out.println("...Returning to main menu...");
                    sc.nextLine();
                    return;
                case 7:
                    int opChoice = chooseParenthesisOperator(sc);
                    if (opChoice == 9) break;
                    ParenthesisResult pr = handleParenthesis(sc);
                    if (pr.expression.isEmpty()) break;
                    switch (opChoice) {
                        case 1:
                            result += pr.result;
                            expression.add(" + (");
                            expression.addAll(pr.expression);
                            expression.add(")");
                            break;
                        case 2:
                            result -= pr.result;
                            expression.add(" - (");
                            expression.addAll(pr.expression);
                            expression.add(")");
                            break;
                        case 3:
                            result *= pr.result;
                            expression.add(" * (");
                            expression.addAll(pr.expression);
                            expression.add(")");
                            break;
                        case 4:
                            if (pr.result == 0) {
                                System.out.println("No div by zero.");
                            } else {
                                result /= pr.result;
                                expression.add(" / (");
                                expression.addAll(pr.expression);
                                expression.add(")");
                            }
                            break;
                        case 5:
                            result = Math.pow(result, pr.result);
                            expression.add(" ^ (");
                            expression.addAll(pr.expression);
                            expression.add(")");
                            break;
                        case 6:
                            if (pr.result == 0) {
                                System.out.println("No mod by zero.");
                            } else {
                                result %= pr.result;
                                expression.add(" % (");
                                expression.addAll(pr.expression);
                                expression.add(")");
                            }
                            break;
                        case 7:
                            if (pr.result < 0) {
                                System.out.println("Sqrt of negative.");
                            } else {
                                result = Math.sqrt(pr.result);
                                expression.add(" √(");
                                expression.addAll(pr.expression);
                                expression.add(")");
                            }
                            break;
                        case 8:
                            if (pr.result < 0 || pr.result != (int) pr.result || pr.result > 20) {
                                System.out.println("Factorial undefined.");
                            } else {
                                result = factorial((int) pr.result);
                                expression.add(" (");
                                expression.addAll(pr.expression);
                                expression.add(")!");
                            }
                            break;
                    }
                    break;
                case 8:
                    if (result < 0) {
                        System.out.println("Sqrt of negative.");
                    } else {
                        result = Math.sqrt(result);
                        expression.addFirst("√(");
                        expression.add(")");
                    }
                    break;
                case 9:
                    System.out.print("Enter exponent: ");
                    double exp = getNextDigit(sc);
                    result = Math.pow(result, exp);
                    expression.add(" ^ " + formatNumber(exp));
                    break;
                case 10:
                    double mod = getNextDigit(sc);
                    if (mod == 0) {
                        System.out.println("Cannot mod by zero.");
                    } else {
                        result %= mod;
                        expression.add(" % ");
                        expression.add(formatNumber(mod));
                    }
                    break;
                case 11:
                    if (expression.size() >= 3) {
                        String lastNumStr = expression.getLast();
                        try {
                            double lastValue = Double.parseDouble(lastNumStr);
                            if (lastValue < 0 || lastValue != (int) lastValue || lastValue > 20) {
                                System.out.println("Factorial undefined.");
                                break;
                            }
                            long factResult = factorial((int) lastValue);
                            String operator = expression.get(expression.size() - 2).trim();
                            switch (operator) {
                                case "+":
                                    result = (result - lastValue) + factResult;
                                    break;
                                case "-":
                                    result = (result + lastValue) - factResult;
                                    break;
                                case "*":
                                    result = (result / lastValue) * factResult;
                                    break;
                                case "/":
                                    result = (result * lastValue) / factResult;
                                    break;
                                default:
                                    System.out.println("Cannot apply ! after " + operator);
                                    continue mainLoop;
                            }
                            expression.add("!");
                        } catch (NumberFormatException e) {
                            System.out.println("Cannot apply factorial here.");
                        }
                    } else {
                        if (result < 0 || result != (int) result || result > 20) {
                            System.out.println("Factorial undefined.");
                        } else {
                            result = factorial((int) result);
                            expression.add("!");
                        }
                    }
                    break;
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n--- Calculator Menu (Queue) ---");
        System.out.println("1.Add(+) 2.Sub(-) 3.Mul(*) 4.Div(/) 5.Result(=)");
        System.out.println("6.Return to Main Menu 7.Parenthesis() 8.Sqrt(√) 9.Power(^) 10.Mod(%) 11.Factorial(!)");
        System.out.print("Choose an operation (1-11): ");
    }

    private static int chooseParenthesisOperator(Scanner sc) {
        System.out.println("Operator for parenthesis: 1.+ 2.- 3.* 4./ 5.^ 6.% 7.√ 8.! 9.Cancel");
        return getIntInput(sc, 1, 9);
    }

    private static ParenthesisResult handleParenthesis(Scanner sc) {
        double result = getFirstDigit(sc);
        ExpressionQueue expr = new ExpressionQueue();
        expr.add(formatNumber(result));

        while (true) {
            System.out.println("\n--()Menu--\n1.+ 2.- 3.* 4./ 5.= 6.Cancel 7.√ 8.^ 9.% 10.!");
            int choice = getIntInput(sc, 1, 10);
            if (choice == 5) return new ParenthesisResult(result, expr);
            if (choice == 6) return new ParenthesisResult(0, new ExpressionQueue());
            double nextDigit;
            switch (choice) {
                case 1:
                    nextDigit = getNextDigit(sc);
                    result += nextDigit;
                    expr.add(" + ");
                    expr.add(formatNumber(nextDigit));
                    break;
                case 2:
                    nextDigit = getNextDigit(sc);
                    result -= nextDigit;
                    expr.add(" - ");
                    expr.add(formatNumber(nextDigit));
                    break;
                case 3:
                    nextDigit = getNextDigit(sc);
                    result *= nextDigit;
                    expr.add(" * ");
                    expr.add(formatNumber(nextDigit));
                    break;
                case 4:
                    nextDigit = getNextDigit(sc);
                    if (nextDigit == 0) {
                        System.out.println("No div by zero.");
                    } else {
                        result /= nextDigit;
                        expr.add(" / ");
                        expr.add(formatNumber(nextDigit));
                    }
                    break;
                case 7:
                    if (result < 0) {
                        System.out.println("Sqrt of negative.");
                    } else {
                        result = Math.sqrt(result);
                        expr.addFirst("√(");
                        expr.add(")");
                    }
                    break;
                case 8:
                    System.out.print("Exponent: ");
                    double exp = getNextDigit(sc);
                    result = Math.pow(result, exp);
                    expr.add(" ^ " + formatNumber(exp));
                    break;
                case 9:
                    nextDigit = getNextDigit(sc);
                    if (nextDigit == 0) {
                        System.out.println("No mod by zero.");
                    } else {
                        result %= nextDigit;
                        expr.add(" % ");
                        expr.add(formatNumber(nextDigit));
                    }
                    break;
                case 10:
                    if (result < 0 || result != (int) result || result > 20) {
                        System.out.println("Invalid for factorial.");
                    } else {
                        result = factorial((int) result);
                        expr.add("!");
                    }
                    break;
            }
        }
    }

    private static double getFirstDigit(Scanner sc) {
        System.out.print("Enter the first digit: ");
        while (!sc.hasNextDouble()) {
            System.out.print("Enter a number: ");
            sc.next();
        }
        double val = sc.nextDouble();
        sc.nextLine();
        return val;
    }

    private static double getNextDigit(Scanner sc) {
        System.out.print("Enter the next digit: ");
        while (!sc.hasNextDouble()) {
            System.out.print("Enter a number: ");
            sc.next();
        }
        double val = sc.nextDouble();
        sc.nextLine();
        return val;
    }

    private static int getIntInput(Scanner sc, int min, int max) {
        while (true) {
            String line = sc.nextLine();
            try {
                int val = Integer.parseInt(line.trim());
                if (val >= min && val <= max) return val;
            } catch (NumberFormatException e) {
                // ignore
            }
            System.out.print("Choose option (" + min + "-" + max + "): ");
        }
    }

    private static long factorial(int n) {
        if (n == 0 || n == 1) return 1;
        long res = 1;
        for (int i = 2; i <= n; i++) res *= i;
        return res;
    }

    private static String formatNumber(double num) {
        if (num == (long) num) {
            return String.valueOf((long) num);
        } else {
            return df.format(num);
        }
    }
}