/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.23.
--------------------------------------------------------------------------------
    Descrpition: This class turns a list of statements into bytecode.
*/

import java.util.*;

public class CodeGenerator {

    private int varCount;
    private Map<String,Integer> varNameToNumber;
    private IntStack tempByteCode;

    //constructor
    public CodeGenerator() {
        varCount = 0;
        varNameToNumber = new HashMap<String,Integer>();
        tempByteCode = new IntStack();
    } //constructor

    public int[] byteCode(Statements program) {
        Statement[] listStmts = program.statements;
        int stackSize = tempByteCode.size();

        for(int i = 0; i < listStmts.length; i++) {
            if(listStmts[i] instanceof Assignement)
                generateAssignement((Assignement)listStmts[i]);
            else if(listStmts[i] instanceof IFStatement)
                generateIf((IFStatement)listStmts[i]);
            else if(listStmts[i] instanceof WhileLoop)
                generateWhile((WhileLoop)listStmts[i]);
            else if(listStmts[i] instanceof PrintStatement)
                generatePrint((PrintStatement)listStmts[i]);
        } //for

        tempByteCode.push(VM.halt);
        int[] toReturn = tempByteCode.toIntArray();
        while(tempByteCode.size() != stackSize)
            tempByteCode.pop();
        return toReturn;
    } //byteCode

    private void generateIf(IFStatement stmt) {
        System.out.println("If statement");

        //generate first the body to know where to jump
        //int[] ifBody = byteCode(stmt.body);
        //int[] elseBody = byteCode(stmt.elseBody);
        //generate 
    } //generateIf

    private void generateWhile(WhileLoop loop) {
        System.out.println("While loop");

        //first generate the body
        int[] loopBody = byteCode(loop.loopBody);

        int start = tempByteCode.size();
        chopBExpr(loop.condition,loopBody.length+1);

        for(int i = 0; i < loopBody.length-1; i++)
            tempByteCode.push(loopBody[i]);

        tempByteCode.push(VM.jmp);
        tempByteCode.push(start-tempByteCode.size());
    } //generateWhile

    private void generateAssignement(Assignement ass) {
        System.out.println("Assignement");
        generateAExpr(ass.value);
        tempByteCode.push(VM.isave);
        tempByteCode.push(varCount);
        varNameToNumber.put(ass.name.tokenValue,varCount);
        varCount++;
    } //generateAssignement

    private void generatePrint(PrintStatement stmt) {
        System.out.println("PrintStatement");
        generateAExpr(stmt.exprToPrint);
        tempByteCode.push(VM.print);
    } //generatePrint

    private void chopBExpr(BExpression expr, int address) {
        Token[] tokens = expr.expr;
        List<Token> tempExpr = new ArrayList<Token>();

        if(Arrays.asList(tokens).contains(new Token("|",TokenType.OR)) ||
           Arrays.asList(tokens).contains(new Token("&",TokenType.AND))) {
            for(int i = 0; i < tokens.length; i++) {
                while(tokens[i].tokenType != TokenType.OR ||
                      tokens[i].tokenType != TokenType.AND ||
                      i >= tokens.length) {

                    tempExpr.add(tokens[i]);
                    i++;
                } //while

                if(i >= tokens.length) {
                    generateBExpr(new BExpression(tempExpr.toArray(new Token[0])),address);
                }
                else if(tokens[i].tokenType == TokenType.AND) {
                    generateBExpr(new BExpression(tempExpr.toArray(new Token[0])),address);
                }
                else if(tokens[i].tokenType == TokenType.OR) {
                    generateBExpr(new BExpression(tempExpr.toArray(new Token[0])),0);
                }
            } //for
        } //if
        else {
            generateBExpr(expr,address);
        }
    } //chopBExpr

    private void generateBExpr(BExpression expr, int address) {
        Token[] tokens = expr.expr;
        List<Token> aexpr = new ArrayList<Token>();
        boolean isNegated = false;
        Token operand;

        for(int i = 0; i < tokens.length; i++) {
            if(tokens[i].tokenType == TokenType.NOT)
                isNegated = true;
            if(tokens[i].tokenType == TokenType.FALSE ||
               tokens[i].tokenType == TokenType.TRUE && isNegated) {
                tempByteCode.push(VM.jmp);
                tempByteCode.push(address);
            }
            else {
                while(tokens[i].tokenType != TokenType.EQUAL &&
                      tokens[i].tokenType != TokenType.LESSTHAN &&
                      tokens[i].tokenType != TokenType.LESSTHANOREQUAL) {
                    System.out.println(tokens[i]);
                    aexpr.add(tokens[i]);
                    i++;
                }

                generateAExpr(new AExpression(aexpr.toArray(new Token[0])));
                operand = tokens[i];
                i++;
                aexpr = new ArrayList<Token>();

                while(i < tokens.length) {
                    aexpr.add(tokens[i]);
                    i++;
                }

                generateAExpr(new AExpression(aexpr.toArray(new Token[0])));

                if(operand.tokenType == TokenType.EQUAL && !isNegated)
                    tempByteCode.push(VM.jmpeq);
                else if(operand.tokenType == TokenType.EQUAL && isNegated)
                    tempByteCode.push(VM.jmpne);
                else if(operand.tokenType == TokenType.LESSTHAN)
                    tempByteCode.push(VM.jmpg);
                else if(operand.tokenType == TokenType.LESSTHANOREQUAL)
                    tempByteCode.push(VM.jmpge);

                tempByteCode.push(address);
            } //else

            
        } //for
    } //generateBExpr

    private void generateAExpr(AExpression expr) {
        Token[] tokens = expr.expr;

        for(int i = 0; i < tokens.length; i++) {
            if(tokens.length == 1)
                loadNumberOrVar(tokens[i]);
            else if(tokens.length == 3){
                loadNumberOrVar(tokens[i]);
                loadNumberOrVar(tokens[i+2]);
                if(tokens[i+1].tokenType == TokenType.PLUS)
                    tempByteCode.push(VM.iadd);
                else if(tokens[i+1].tokenType == TokenType.MINUS)
                    tempByteCode.push(VM.isub);
                else
                    tempByteCode.push(VM.imul);
                i += 2;
            }
            else if(i == 0 && tokens[i+1].tokenType == TokenType.MULTIPLICATION) {
                loadNumberOrVar(tokens[i]);
                loadNumberOrVar(tokens[i+2]);
                tempByteCode.push(VM.imul);
                i += 2;
            } //if
            else if(i == 0 && (tokens[i+1].tokenType == TokenType.PLUS
                               || tokens[i+1].tokenType == TokenType.MINUS)) {
                if(tokens[i+3].tokenType == TokenType.PLUS ||
                   tokens[i+3].tokenType == TokenType.MINUS) {
                    loadNumberOrVar(tokens[i]);
                    loadNumberOrVar(tokens[i+2]);
                    if(tokens[i+1].tokenType == TokenType.PLUS)
                        tempByteCode.push(VM.iadd);
                    else
                        tempByteCode.push(VM.isub);
                    i += 2;
                }
                else {
                    loadNumberOrVar(tokens[i+2]);
                    loadNumberOrVar(tokens[i+4]);
                    tempByteCode.push(VM.imul);
                    int offset = 5;
                    while(tokens[i+offset].tokenType != TokenType.PLUS ||
                          tokens[i+offset].tokenType != TokenType.MINUS) {
                        loadNumberOrVar(tokens[i+offset+1]);
                        tempByteCode.push(VM.imul);
                        offset += 2;
                    } //while
                    loadNumberOrVar(tokens[i]);
                    if(tokens[i+1].tokenType == TokenType.PLUS)
                        tempByteCode.push(VM.iadd);
                    else
                        tempByteCode.push(VM.isub);
                    i += offset;
                } //else
            }
            else if(tokens[i].tokenType == TokenType.MULTIPLICATION) {
                loadNumberOrVar(tokens[i+1]);
                tempByteCode.push(VM.imul);
                i++;
            }
            else if(tokens[i].tokenType == TokenType.PLUS ||
                    tokens[i].tokenType == TokenType.MINUS) {

                if(tokens[i+2].tokenType == TokenType.PLUS ||
                   tokens[i+2].tokenType == TokenType.MINUS) {
                    loadNumberOrVar(tokens[i+1]);
                    if(tokens[i].tokenType == TokenType.PLUS)
                        tempByteCode.push(VM.iadd);
                    else
                        tempByteCode.push(VM.isub);
                } //if
                else {
                    loadNumberOrVar(tokens[i+1]);
                    loadNumberOrVar(tokens[i+3]);
                    tempByteCode.push(VM.imul);
                    int offset = 4;
                    while(tokens[i+offset].tokenType != TokenType.PLUS ||
                          tokens[i+offset].tokenType != TokenType.MINUS) {
                        loadNumberOrVar(tokens[i+offset+1]);
                        tempByteCode.push(VM.imul);
                        offset += 2;
                    } //while
                    loadNumberOrVar(tokens[i+1]);
                    if(tokens[i].tokenType == TokenType.PLUS)
                        tempByteCode.push(VM.iadd);
                    else
                        tempByteCode.push(VM.isub);
                    i += offset;
                }//else
            } //else if
            else 
                System.out.println(
                            "Error parsing arithmetic expression: "+tokens[i]);
        } //for
    } //generateAExpr

    private void loadNumberOrVar(Token token) {
        if(token.tokenType == TokenType.INT) {
            tempByteCode.push(VM.iconst);
            tempByteCode.push(Integer.parseInt(token.tokenValue));
        }
        else if (token.tokenType == TokenType.NAME) {
            tempByteCode.push(VM.iload);
            tempByteCode.push(varNameToNumber.get(token.tokenValue));
        }
        else
            System.out.println("Not a name or number: "+token);
    } //loadNumberOrVar
} //CodeGenerator