/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

public interface TokenDefaultFields {

	public String getAlfa(); // string format token definition

	public TokenGroup getGroup();

    public int getPrecedence(); // precedence for expression evolution

}
