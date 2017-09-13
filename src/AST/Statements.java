/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.18.
--------------------------------------------------------------------------------
    Description: Data structure for representing multiple statements.
*/

import java.util.ArrayList;
import java.util.List;

public class Statements {

    public final Statement[] statements;
    private static boolean isCodeOK = true;

    //constructor
    public Statements(Token[] tokens) {
        //TODO validity check

        //temporary list of statements
        List<Statement> tempStatements = new ArrayList<Statement>();

        for(int i = 0; i < tokens.length; i++) {
            switch(tokens[i].tokenType) {
                case IF:

                    /*There are three parts that need to be extracted:
                        °The condition
                        °The block where it is true
                        °The block where it is false
                        (it is the condition)

                        So in three parts I am doing the same thing.
                        Extracting tokens from parentheses with a stack
                        based solution.

                        Do not think about DRY. I need the i index of the
                        for loop.
                    */
                    //temporary holding lists while building the arrays
                    List<Token> condTokens = new ArrayList<Token>();
                    List<Token> bodyTokens = new ArrayList<Token>();
                    List<Token> elseTokens = new ArrayList<Token>();

                    //first getting the condition
                    if(tokens[i+1].tokenType == TokenType.OPAREN) {
                        int pStack = 1;
                        i +=2 ;
                        while (pStack != 0) {
                            if(tokens[i].tokenType == TokenType.OPAREN) 
                                pStack++;
                            else if(tokens[i].tokenType == TokenType.CPAREN) 
                                pStack--;

                            if(pStack != 0)
                                condTokens.add(tokens[i]);

                            i++;
                        } //while
                        //now tokens[i] is the then and tokens[i+1] is {
                        //then getting the body of the if
                        if(tokens[i].tokenType == TokenType.THEN &&
                            tokens[i+1].tokenType == TokenType.LBRACE) {
                            int bStack = 1;
                            i += 2;
                            while(bStack != 0) {
                                if(tokens[i].tokenType == TokenType.LBRACE) 
                                    bStack++;
                                
                                else if(tokens[i].tokenType == TokenType.RBRACE) 
                                    bStack--;

                                if(bStack != 0)
                                    bodyTokens.add(tokens[i]);

                                i++;
                            } //while
                        } //if
                        else {
                            handleError("Missing then or {\n On line: "+tokens[i].lineNo);
                        }
                        //lastly getting the part after the else
                        if(i >= tokens.length)
                            handleError("Unexpected end of file. Missing else?");
                        if(tokens[i].tokenType == TokenType.ELSE && 
                            tokens[i+1].tokenType == TokenType.LBRACE) {
                            int bStack = 1;
                            i += 2;
                            while(bStack != 0) {
                                if(tokens[i].tokenType == TokenType.LBRACE)
                                    bStack++;
                                else if(tokens[i].tokenType == TokenType.RBRACE)
                                    bStack--;

                                if(bStack != 0) {
                                    elseTokens.add(tokens[i]);
                                    i++;
                                }

                            } //while
                        } //if
                        else {
                            handleError("Missing else or {. \n On line: "+tokens[i]); }
                    } //if
                    else {
                        handleError("Missing '(' after if. \n On line: "+tokens[i].lineNo);
                    }
                    tempStatements.add(new IFStatement(condTokens.toArray(new Token[0]),
                        bodyTokens.toArray(new Token[0]),elseTokens.toArray(new Token[0])));
                    break;
                case WHILE:
                    /* So this one has a:
                        °Condition
                        °Loop body

                        Same tactic as with the if statement. Stack based extraction
                        within parentheses.
                    */

                    //temporary holding list for the condition and body
                    List<Token> tempCond = new ArrayList<Token>();
                    List<Token> tempBody = new ArrayList<Token>();

                    if(tokens[i+1].tokenType == TokenType.OPAREN) {
                        int pStack = 1;
                        i += 2;

                        while(pStack != 0) {
                            if(tokens[i].tokenType == TokenType.OPAREN)
                                pStack++;
                            else if(tokens[i].tokenType == TokenType.CPAREN)
                                pStack--;

                            if(pStack != 0) 
                                tempCond.add(tokens[i]);

                            i++;
                        } //while
                    } //if
                    else {
                        handleError("No brackets after while. \nOn line: "+tokens[i].lineNo);
                    }

                    if(tokens[i].tokenType == TokenType.DO &&
                        tokens[i+1].tokenType == TokenType.LBRACE) {
                        int bStack = 1;
                        i +=2 ;

                        while(bStack != 0) {
                            if(tokens[i].tokenType == TokenType.LBRACE)
                                bStack++;
                            else if(tokens[i].tokenType == TokenType.RBRACE)
                                bStack--;

                            if(bStack != 0) {
                                tempBody.add(tokens[i]);
                                i++;
                            }

                        } //while

                    BExpression cond = new BExpression(tempCond.toArray(new Token[0]));
                    Statements bodyStmts = new Statements(tempBody.toArray(new Token[0]));

                    tempStatements.add(new WhileLoop(cond,bodyStmts));
                    } //if
                    else {
                        handleError("Missing do or { after while.\nOn line: "+tokens[i].lineNo);
                    }

                    break;
                case NAME:
                    if(tokens[i+1].tokenType == TokenType.ASSIGNEMENT) {
                        Token name = tokens[i];
                        i+=2;
                        List<Token> tempTokens = new ArrayList<Token>();
                        while(tokens[i].tokenType != TokenType.SEMICOLON) {
                            if(!isAExpr(tokens[i])) {
                                handleError("Unexpected expression: " + tokens[i].tokenValue
                                        +". Missing semicolon?"
                                        +"\nOn line: "+tokens[i].lineNo);
                            }
                            tempTokens.add(tokens[i]);
                            i++;
                        } //while

                        tempStatements.add(new Assignement(name, new AExpression(tempTokens.toArray(new Token[0]))));
                    } //if
                    else {
                        handleError("Unexpected symbol: " + tokens[i].tokenValue
                                + "\nOn line: " + tokens[i].lineNo);
                    }
                    break;
                case PRINT:
                    List<Token> tempTokens = new ArrayList<Token>();

                    if(tokens[i+1].tokenType != TokenType.OPAREN) {
                        handleError("Missing ( after print\nOn line: "+tokens[i+1].lineNo);
                    }
                    int lineNoOfBracket = tokens[i+1].lineNo;
                    int pStack = 1;
                    i += 2;
                    while(pStack != 0 && i < tokens.length) {
                        if(tokens[i].tokenType == TokenType.OPAREN)
                            pStack++;
                        else if(tokens[i].tokenType == TokenType.CPAREN)
                            pStack--;

                        if(pStack != 0)
                            tempTokens.add(tokens[i]);

                        i++;

                        if(i >= tokens.length) {
                            if(tokens[i-1].tokenType == TokenType.CPAREN)
                                handleError("Missing semicolon after print. On line : "+lineNoOfBracket);
                            else
                                handleError("Unexpected end of file. Unclosed bracket on line : "+lineNoOfBracket);
                        }
                    } //while

                    tempStatements.add(new PrintStatement(new AExpression(tempTokens.toArray(new Token[0]))));
                    break;
                case SKIP:
                    break;
                default:
                    //Something went horribly wrong
                    break;
            } //switch
        } //for
        statements = tempStatements.toArray(new Statement[0]);
    } //constructor

    @Override
    public String toString() {
        String statementString = "";

        for(Statement stmt : statements)
            statementString += stmt.toString() + "\n";

        return statementString;
    }

    private boolean isAExpr(Token token) {
        boolean isTrue = false;
        if(token.tokenType == TokenType.PLUS ||
                token.tokenType == TokenType.MINUS ||
                token.tokenType == TokenType.MULTIPLICATION ||
                token.tokenType == TokenType.NAME ||
                token.tokenType == TokenType.INT)
            isTrue = true;

        return isTrue;

    }

    private void handleError(String message) {
        System.err.println(message);
        isCodeOK = false;
    }

    public boolean isRunnable() {
        return isCodeOK;
    }
} //Statements