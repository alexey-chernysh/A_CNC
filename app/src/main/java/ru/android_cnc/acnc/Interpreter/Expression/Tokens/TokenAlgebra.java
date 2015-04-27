/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;

public enum TokenAlgebra implements TokenDefaultFields {
	
	ABS ("ABS", TokenGroup.FUNCTION, -1){
		@Override
		public double evaluate(double x){
			return Math.abs(x);
		};
	},
	
	ACOS("ACOS", TokenGroup.FUNCTION, -1){
		@Override
		public double evaluate(double x){
			return Math.acos(x)*180.0/Math.PI;
		};
	},
	
	ASIN("ASIN", TokenGroup.FUNCTION, -1){
		@Override
		public double evaluate(double x){
			return Math.asin(x)*180.0/Math.PI;
		};
	},
	
	ATAN("ATAN", TokenGroup.FUNCTION, -1){
		@Override
		public double evaluate(double x, double y){
			return Math.atan2(x, y)*180.0/Math.PI;
		};
	},
	
	COS("COS", TokenGroup.FUNCTION, -1){
		@Override
		public double evaluate(double x){
			return Math.cos(Math.PI*x/180.0);
		};
	},
	
	FIX("FIX", TokenGroup.FUNCTION, -1){
		@Override
		public double evaluate(double x){
			return Math.floor(x);
		};
	},
	
	FUP("FUP", TokenGroup.FUNCTION, -1){
		@Override
		public double evaluate(double x){
			return (Math.floor(x)+1.0);
		};
	},
	
	ROUND("ROUND", TokenGroup.FUNCTION, -1){
		@Override
		public double evaluate(double x){
			return Math.round(x);
		};
	},
	
	SIN("SIN", TokenGroup.FUNCTION, -1){
		@Override
		public double evaluate(double x){
			return Math.sin(Math.PI*x/180.0);
		};
	},
	
	TAN("TAN", TokenGroup.FUNCTION, -1){
		@Override
		public double evaluate(double x){
			return Math.tan(Math.PI*x/180.0);
		};
	},
	
	SQRT("SQRT", TokenGroup.FUNCTION, -1){
		@Override
		public double evaluate(double x) throws InterpreterException{
			if(x<0.0) throw new InterpreterException("Square root from negative number");
			return Math.sqrt(x);
		};
	},
	
	EXP("EXP", TokenGroup.FUNCTION, -1){
		@Override
		public double evaluate(double x){
			return Math.exp(x);
		};
	},
	
	LN("LN", TokenGroup.FUNCTION, -1){
		@Override
		public double evaluate(double x) throws InterpreterException{
			if(x<0.0) throw new InterpreterException("Log from negative number");
			return Math.log(x);
		};
	},	
	
	XOR ("XOR", TokenGroup.ALGEBRA,	9){
		@Override
		public double evaluate(double x, double y){
			if((x != 0.0)^(y != 0.0)) return 1.0;
			else return 0.0;
		};
	},
	
	OR ("OR", TokenGroup.ALGEBRA, 10){
		@Override
		public double evaluate(double x, double y){
			if((x != 0.0)||(y != 0.0)) return 1.0;
			else return 0.0;
		};
	},
	
	AND ("AND", TokenGroup.ALGEBRA,	8){
		@Override
		public double evaluate(double x, double y){
			if((x != 0.0)&&(y != 0.0)) return 1.0;
			else return 0.0;
		};
	},
	
	MOD ("MOD", TokenGroup.ALGEBRA,	3){
		@Override
		public double evaluate(double x, double y){
			return x % y;
		};
	},
	
	POW("**", TokenGroup.ALGEBRA, 1){
		@Override
		public double evaluate(double x, double y){
			return Math.pow(x, y);
		};
	},
	
	PLUS("+", TokenGroup.ALGEBRA, 4){
		@Override
		public double evaluate(double x, double y){
			return (x+y);
		};
	},
	
	MINUS("-", TokenGroup.ALGEBRA, 4){
		@Override
		public double evaluate(double x, double y){
			return (x-y);
		};
	},
	
	MULTIPLY("*", TokenGroup.ALGEBRA, 3){
		@Override
		public double evaluate(double x, double y){
			return (x*y);
		};
	},
	
	DIVIDE("/", TokenGroup.ALGEBRA, 3){
		@Override
		public double evaluate(double x, double y) throws InterpreterException{
			if(y == 0.0) throw new InterpreterException("Divide by zero");
			return (x/y);
		};
	},
	
	VAR("#", TokenGroup.MISC, -1),
	
	LEFT_BRACKET("[", TokenGroup.MISC, 1),
	
	RIGHT_BRACKET("]", TokenGroup.MISC, 1),
	
	ASSIGN("=", TokenGroup.MISC, 13);

	private String alfa_;
	private TokenGroup group_;
	private int precedence_; // in order http://en.wikipedia.org/wiki/Order_of_operations
	
	private TokenAlgebra(String a, TokenGroup g, int p){
		alfa_ = a;
		group_ = g;
		precedence_ = p;
	}
	
	public double evaluate(double x) throws InterpreterException{
		throw new InterpreterException("Token interpretation error");
	}	

	public double evaluate(double x, double y) throws InterpreterException{
		throw new InterpreterException("Token interpretation error");
	}

	@Override
	public String getAlfa() {
		return alfa_;
	}

	@Override
	public TokenGroup getGroup() {
		return group_;
	}

	@Override
	public int getPrecedence() {
		return precedence_;
	}	

}
