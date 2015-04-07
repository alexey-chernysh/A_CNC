package ru.android_cnc.acnc.test;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.android_cnc.acnc.Interpreter.Expression.Tokens.Token;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenSequence;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenUnlexedText;
import ru.android_cnc.acnc.Interpreter.InterpreterException;

/**
 * Created by Sales on 07.04.2015.
 */

public class InterpreterExceptionTest extends TestCase {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void lexerExceptionTest() throws InterpreterException {

        thrown.expect(InterpreterException.class);
        thrown.expectMessage("Abracadabra parsing");
        TokenSequence tokenSequence = new TokenSequence("ABRACADABRA");

    }
}
