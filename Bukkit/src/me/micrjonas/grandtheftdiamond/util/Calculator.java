package me.micrjonas.grandtheftdiamond.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Calculator {
	
	private Calculator() { }
	
	private final static ScriptEngine calculator = new ScriptEngineManager().getEngineByName("JavaScript");
	
	
	/**
	 * Calculates the given String
	 * @param calculation The calculation
	 * @return The result of the given calculation. Double.NaN if the method failed to calculate the {@code String}
	 */
	public static double calculate(String calculation) {
		try {
			return (double) calculator.eval(calculation);
		} 
		catch (ScriptException e) {
			return Double.NaN;
		}
	}
	
	/**
	 * Calculates the given String
	 * @param calculation The calculation
	 * @param exceptionReturn exeptionReturn will be returned if the result of the calculation is Double.NaN
	 * @return The result of the given calculation
	 */
	public static double calculateOrDefault(String calculation, double exceptionReturn) {
		double result = calculate(calculation);
		if (result == result) {
			return result;
		}
		return exceptionReturn;
	}
	
	public int calculate(CalculateConstant constant, int value0, int value1) {
		return constant.calculate(value0, value1);
	}
	
	public double calculate(CalculateConstant constant, double value0, double value1) {
		return constant.calculate(value0, value1);
	}
	
}

