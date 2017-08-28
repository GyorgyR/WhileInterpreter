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
        int[] toReturn = tempByteCode.getInterval(stackSize, tempByteCode.size()-1);
        while(tempByteCode.size() != stackSize)
            tempByteCode.pop();
        return toReturn;
    } //byteCode

    private void generateIf(IFStatement stmt) {
        //System.out.println("If statement");

        //generate first the body to know where to jump
        int[] ifBody = byteCode(stmt.body);
        int[] elseBody = byteCode(stmt.elseBody);

        chopBExpr(stmt.condition, ifBody.length + 3);

        for(int i = 0; i < ifBody.length - 1; i++)
            tempByteCode.push(ifBody[i]);

        tempByteCode.push(VM.jmp);
        tempByteCode.push(elseBody.length + 1);

        for(int i = 0; i < elseBody.length -  1; i++)
            tempByteCode.push(elseBody[i]);
        //generate 
    } //generateIf

    private void generateWhile(WhileLoop loop) {
        //System.out.println("While loop");

        //first generate the body
        int[] loopBody = byteCode(loop.loopBody);

        int start = tempByteCode.size();
        chopBExpr(loop.condition,loopBody.length+3);

        for(int i = 0; i < loopBody.length-1; i++)
            tempByteCode.push(loopBody[i]);

        tempByteCode.push(VM.jmp);
        tempByteCode.push(start-tempByteCode.size()+1);
    } //generateWhile

    private void generateAssignement(Assignement ass) {
        //System.out.println("Assignement");
        generateAExpr(ass.value);
        tempByteCode.push(VM.isave);
        if(varNameToNumber.containsKey(ass.name.tokenValue)) {
            tempByteCode.push(varNameToNumber.get(ass.name.tokenValue));
        }
        else {
            tempByteCode.push(varCount);
            varNameToNumber.put(ass.name.tokenValue,varCount);
            varCount++;
        }
    } //generateAssignement

    private void generatePrint(PrintStatement stmt) {
        //System.out.println("PrintStatement");
        generateAExpr(stmt.exprToPrint);
        tempByteCode.push(VM.print);
    } //generatePrint

    private void chopBExpr(BExpression expr, int address) {
        Token[] tokens = expr.expr;
        List<Token> tempExpr = new ArrayList<Token>();

        if(containsOrAnd(tokens)) {
            for(int i = 0; i < tokens.length; i++) {
                while(i < tokens.length &&
                      tokens[i].tokenType != TokenType.OR &&
                      tokens[i].tokenType != TokenType.AND ) {

                    tempExpr.add(tokens[i]);
                    i++;
                } //while

                if(i >= tokens.length) {
                    generateBExpr(new BExpression(tempExpr.toArray(new Token[0])),address);
                }
                else if(tokens[i].tokenType == TokenType.AND) {
                    int initStackSize = tempByteCode.size();
                    chopBExpr(new BExpression(Arrays.copyOfRange(tokens,i+1,tokens.length)),0);
                    int afterSize = tempByteCode.size();
                    while(tempByteCode.size() != initStackSize)
                        tempByteCode.pop();
                    generateBExpr(new BExpression(tempExpr.toArray(new Token[0])),afterSize-initStackSize + address);
                }
                else if(tokens[i].tokenType == TokenType.OR) {
                    int initStackSize = tempByteCode.size();
                    chopBExpr(new BExpression(Arrays.copyOfRange(tokens,i+1,tokens.length)),0);
                    int afterSize = tempByteCode.size();
                    while(tempByteCode.size() != initStackSize)
                        tempByteCode.pop();
                    tempExpr.add(0,new Token("!", TokenType.NOT));
                    generateBExpr(new BExpression(tempExpr.toArray(new Token[0])),afterSize-initStackSize + 2);
                }
                //System.out.println(tempExpr);
                tempExpr = new ArrayList<Token>();
            } //for

        } //if
        else {
            generateBExpr(expr,address);
        }
    } //chopBExpr

    private boolean containsOrAnd(Token[] tokens) {
        boolean contains = false;

        for(Token item : tokens)
            if(item.tokenType == TokenType.OR || item.tokenType == TokenType.AND)
                contains = true;

        return contains;
    } //containsOrAnd

    private void generateBExpr(BExpression expr, int address) {
        Token[] tokens = expr.expr;
        List<Token> aexpr = new ArrayList<Token>();
        boolean isNegated = false;
        Token operand;

        for(int i = 0; i < tokens.length; i++) {
            if(tokens[i].tokenType == TokenType.NOT)
                isNegated = !isNegated;
            else if(tokens[i].tokenType == TokenType.FALSE ||
               tokens[i].tokenType == TokenType.TRUE && isNegated) {
                tempByteCode.push(VM.jmp);
                tempByteCode.push(address);
            }
            else {
                while(tokens[i].tokenType != TokenType.EQUAL &&
                      tokens[i].tokenType != TokenType.LESSTHAN &&
                      tokens[i].tokenType != TokenType.LESSTHANOREQUAL) {
                    //System.out.println(tokens[i]);
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
                    tempByteCode.push(VM.jmpne);
                else if(operand.tokenType == TokenType.EQUAL && isNegated)
                    tempByteCode.push(VM.jmpeq);
                else if(operand.tokenType == TokenType.LESSTHAN && !isNegated)
                    tempByteCode.push(VM.jmpg);
                else if(operand.tokenType == TokenType.LESSTHAN && isNegated)
                    tempByteCode.push(VM.jmpn);
                else if(operand.tokenType == TokenType.LESSTHANOREQUAL && !isNegated)
                    tempByteCode.push(VM.jmpge);
                else if(operand.tokenType == TokenType.LESSTHANOREQUAL && isNegated)
                    tempByteCode.push(VM.jmpen);

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