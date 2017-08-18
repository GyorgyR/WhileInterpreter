/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.18.
--------------------------------------------------------------------------------
    Description: Data structure for representing multiple statements.
*/

import java.util.*;

public class Statements {

    private Statement[] statements;

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
                        //TODO CHECK THEM
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
                        //TODO error reporting
                        //lastly getting the part after the else
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
                    } //if
                    else 
                        //TODO throw exception
                        System.out.println("Something wrong: after if no OPAREN");
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

                    //TODO error handling
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
                    else 
                        System.out.println("No brackets after while");

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
                    else 
                        System.out.println("No braces after condition");

                    break;
                case NAME:
                    if(tokens[i+1].tokenType == TokenType.ASSIGNEMENT) {
                        Token name = tokens[i];
                        i+=2;
                        List<Token> tempTokens = new ArrayList<Token>();
                        while(tokens[i].tokenType != TokenType.SEMICOLON) {
                            tempTokens.add(tokens[i]);
                            i++;
                        } //while

                        tempStatements.add(new Assignement(name, new AExpression(tempTokens.toArray(new Token[0]))));
                    } //if
                    break;
                case PRINT:
                    List<Token> tempTokens = new ArrayList<Token>();
                    //TODO error checking
                    int pStack = 1;
                    i += 2;
                    while(pStack != 0) {
                        if(tokens[i].tokenType == TokenType.OPAREN)
                            pStack++;
                        else if(tokens[i].tokenType == TokenType.CPAREN)
                            pStack--;

                        if(pStack != 0)
                            tempTokens.add(tokens[i]);

                        i++;
                    } //while
                    //i++;
                    tempStatements.add(new PrintStatement(new AExpression(tempTokens.toArray(new Token[0]))));
                    break;
                case SKIP:
                    break;
                default:
                    System.out.println("Ooops, skipped: "+tokens[i].toString());
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
} //Statements