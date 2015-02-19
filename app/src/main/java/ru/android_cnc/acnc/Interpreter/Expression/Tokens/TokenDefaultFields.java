/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

public interface TokenDefaultFields {

	public String getAlfa(); // string format token difinition

	public TokenGroup getGroup(); 

    int getPrecedence(); // precedence for expresion evolution

}
