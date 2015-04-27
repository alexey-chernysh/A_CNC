package ru.android_cnc.acnc.test;

import junit.framework.TestCase;

import org.junit.Test;

import ru.android_cnc.acnc.Interpreter.Expression.Tokens.Token;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenComment;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenSequence;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenUnlexedText;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;

public class TokenSequenceTest extends TestCase {

    @Test
    public void testTokenComments(){
        tryForComments("G92 X0 Y0 (MSG Set current position to null)");
        tryForComments("/G92 X0 Y0 (MSG Set current position to null)");
        tryForComments(";G92 X0 Y0 (MSG Set current position to null)");
        tryForComments("(G92 X0 Y0) (MSG Set current position to null)");
        tryForComments("G92 X0 Y0 (Set current position to null)");
    }

    private void tryForComments(String source){
        TokenSequence tokenSequence;
        try{
            boolean commentFounded = false;
            tokenSequence = new TokenSequence(source);
            int size = tokenSequence.tokenList.size();
            org.junit.Assert.assertTrue(size>0);
            for(int i=0; i<size; i++){
                Token currentToken = tokenSequence.tokenList.get(i);
                if(currentToken instanceof TokenComment){
                    commentFounded = true;
                }
                org.junit.Assert.assertFalse(currentToken instanceof TokenUnlexedText);
            }
            assertTrue(commentFounded);
        } catch (InterpreterException ie){
            org.junit.Assert.assertTrue("failure - exception thrown", false);
        };
    }

}