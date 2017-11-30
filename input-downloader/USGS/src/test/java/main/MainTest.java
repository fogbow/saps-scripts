package main;

import org.junit.*;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class MainTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void zeroArgTest() throws Exception {
        exit.expectSystemExitWithStatus(6);
        String[] args = {};
        Main.checkNumberOfArgs(args);
    }

    @Test
    public void oneArgTest() throws Exception {
        exit.expectSystemExitWithStatus(6);
        String[] args = {"a"};
        Main.checkNumberOfArgs(args);
    }

    @Test
    public void fourArgTest() throws Exception {
        exit.expectSystemExitWithStatus(6);
        String[] args = {"a", "a", "a", "a"};
        Main.checkNumberOfArgs(args);
    }

    @Test
    public void fiveArgTest() throws Exception {
        String[] args = {"a", "a", "a", "a", "a"};
        Main.checkNumberOfArgs(args);
    }

    @Test
    public void sixArgTest() throws Exception {
        exit.expectSystemExitWithStatus(6);
        String[] args = {"a", "a", "a", "a", "a", "a"};
        Main.checkNumberOfArgs(args);
    }
}
