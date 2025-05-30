package com.kousenit.langchain4j;

import dev.langchain4j.agent.tool.Tool;

/**
 * CalculatorTool demonstrates multiple related tools in one class.
 * <p>
 * This tool provides basic mathematical operations with proper error handling.
 * It shows how to implement multiple @Tool methods within a single class
 * and demonstrates error handling for edge cases like division by zero.
 * <p>
 * Used in Lab 6: AI Tools exercises.
 */
public class CalculatorTool {
    
    @Tool("Add two numbers")
    public double add(double a, double b) {
        return a + b;
    }
    
    @Tool("Subtract the second number from the first number")
    public double subtract(double a, double b) {
        return a - b;
    }
    
    @Tool("Multiply two numbers")
    public double multiply(double a, double b) {
        return a * b;
    }
    
    @Tool("Divide the first number by the second number")
    public double divide(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return a / b;
    }
    
    @Tool("Calculate the power of a number (base raised to exponent)")
    public double power(double base, double exponent) {
        return Math.pow(base, exponent);
    }
    
    @Tool("Calculate the square root of a number")
    public double sqrt(double number) {
        if (number < 0) {
            throw new IllegalArgumentException("Cannot calculate square root of negative number");
        }
        return Math.sqrt(number);
    }
    
    @Tool("Calculate the percentage of a number")
    public double percentage(double number, double percent) {
        return (number * percent) / 100.0;
    }
}