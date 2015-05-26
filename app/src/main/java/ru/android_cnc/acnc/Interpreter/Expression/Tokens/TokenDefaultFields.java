/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

public interface TokenDefaultFields {

	String getAlfa(); // string format token definition

	TokenGroup getGroup();

    int getPrecedence(); // precedence for expression evolution

}
