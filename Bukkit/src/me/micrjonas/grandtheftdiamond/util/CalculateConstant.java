package me.micrjonas.grandtheftdiamond.util;

public enum CalculateConstant {
	
	ADD {
		@Override
		public double calculate(double value0, double value1) {
			return value0 + value1;
		}
	},
	
	SUBTRACT {
		@Override
		public double calculate(double value0, double value1) {
			return value0 - value1;
		}
	},
	
	MULTIPLY {
		@Override
		public double calculate(double value0, double value1) {
			return value0 * value1;
		}
	},
	
	DIVIDE {
		@Override
		public double calculate(double value0, double value1) {
			return value0 / value1;
		}
	};
	
	public int calculate(int value0, int value1) {
		return (int) calculate((double) value0, (double) value1) ;
	}
	
	public abstract double calculate(double value0, double value1);

}
