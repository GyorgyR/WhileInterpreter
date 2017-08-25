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
        return tempByteCode.toIntArray();
    } //byteCode

    private void generateIf(IFStatement stmt) {
        System.out.println("If statement");

        //generate first the body to know where to jump
        //generate 
    } //generateIf

    private void generateWhile(WhileLoop loop) {
        System.out.println("While loop");

        //first generate the body
        int[] loopBody = byteCode(loop.loopBody);
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