/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.17.
--------------------------------------------------------------------------------
    Descrpition: Entrypoint of the program.
*/

public class Main {

    public static void main(String[] args) {
        Lexer lexer = new Lexer();

        //TODO argument check
        Token[] tokenList = lexer.tokens(args[0]);
        AST ast = new AST(tokenList);
        CodeGenerator cG = new CodeGenerator();
        VM vm = new VM(10,cG.byteCode(ast.theProgram));
        vm.start();
    } //main
} //Main