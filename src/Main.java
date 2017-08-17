/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.17.
--------------------------------------------------------------------------------
    Descrpition: ENtrypoint of the program.
*/

public class Main {

    public static void main(String[] args) {
        Lexer lexer = new Lexer();

        //TODO argument check
        Token[] tokenList = lexer.tokens(args[0]);
    } //main
} //Main