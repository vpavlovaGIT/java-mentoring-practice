package main.java.ru.vpavlova.algorithms;

import java.util.Stack;

/**
 * Дана строка s, содержащая только символы '(', ')', '{', '}', '[' и ']'.
 * Необходимо определить, является ли входная строка валидной, т. е. вернуть true или false
 *
 * @example
 * Input: "()"       Output: true
 * Input: "()[]{}"   Output: true
 * Input: "(]"       Output: false
 * Input: "([])"     Output: true
 * Input: "([)]"     Output: false
 */
public class ValidParentheses {
    // асимптотическая сложность O(n), где n — длина строки
    public boolean isValid(String s) {
        // Используем стек для хранения открывающих скобок,
        // так как стек использует принцип LIFO "последний пришел — первый ушел"
        // и когда встречаем закрывающую — проверяем, соответствует ли она последней открытой.
        Stack<Character> stack = new Stack<>(); // создаем стек для хранения символов (Character)
        // Создаем цикл по символам строки
        // Преобразуем строку в массив символов - toCharArray()
        for (char c : s.toCharArray()) {
            // Проверяем на открывающую скобку
            // Если символ "c" — это (, { или [, помещаем его в вершину стека (метод push())
            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);
                // Обработка закрывающей скобки
            } else {
                if (stack.isEmpty()) return false; //  если стек пуст, то строка невалидна
                // Извлекаем последнюю открывающую скобку из стека и и удаляем её из стека (метод pop())
                char top = stack.pop();
                // top - это последняя открывающаяся скобка из стека
                // и проверяем, соответствует ли она текущей закрывающей скобке "с"
                // Если нет, строка невалидна
                if (!isMatchingPair(top, c)) { // проверяем соответствие скобок
                    return false; // несоответствие типов (например, '(' и ']')
                }
            }
        }
        return stack.isEmpty(); // проверка пустоты стека после обработки
        // true, если все скобки закрыты (например, для "()[]").
        // false, если остались незакрытые (например, для "(")
    }

    // Вспомогательный метод isMatchingPair, который возвращает true, если открывающая и закрывающая скобки одного типа
    private boolean isMatchingPair(char opening, char closing) {
        return (opening == '(' && closing == ')') ||
                (opening == '{' && closing == '}') ||
                (opening == '[' && closing == ']');
    }

    public static void main(String[] args) {
        ValidParentheses validator = new ValidParentheses();
        // Проверка разных вариантов из условия
        System.out.println(validator.isValid("()"));      // true
        System.out.println(validator.isValid("()[]{}"));  // true
        System.out.println(validator.isValid("(]"));      // false
        System.out.println(validator.isValid("([])"));    // true
        System.out.println(validator.isValid("([)]"));    // false
    }
}
