package ru.android_cnc.acnc.test;

/**
 * Created by Sales on 07.04.2015.
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CNCPointTest.class,
        TokenSequenceTest.class,
        InterpreterExceptionTest.class
})

public class JunitTestSuite {
    // the class remains empty,
    // used only as a holder for the above annotations
}
