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

        if(!ast.theProgram.isRunnable()) {
            System.exit(1);
        }
        CodeGenerator cG = new CodeGenerator();

        int[] byteCode = cG.byteCode((ast.theProgram));

        if(!cG.isRunnable()) {
            System.exit(1);
        }
        VM vm = new VM(cG.varCount(),byteCode);
        vm.start();
    } //main
} //Main