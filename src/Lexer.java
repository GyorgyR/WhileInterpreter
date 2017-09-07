/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.17.
--------------------------------------------------------------------------------
    Descrpition: The object for lexical analysis.
*/

import java.io.*;
import java.util.*;

public class Lexer {

    private BufferedReader reader;
    private List<String> sourceFileTokens = new ArrayList<String>();
    private List<Token> tokens = new ArrayList<Token>();

    //empty constructor
    public Lexer() {super();}

    public Token[] tokens(String filepath) {
        readFile(filepath);
        enhanceSourceFileTokens();
        createTokenList();
        //printTokenList();
        return tokens.toArray(new Token[0]);
    } //tokens

    private void readFile(String filepath) {
        try {
            reader = new BufferedReader(new FileReader(new File(filepath)));

            String line = reader.readLine();

            while(line != null) {
                char[] characters = line.toCharArray();

                String word = "";

                for(char item : characters) {
                    switch(item) {
                        case ' ':
                        case '\t':
                        case '\n':
                            if(!word.equals("")) {
                                sourceFileTokens.add(word);
                                word = "";
                            }
                            break;
                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case ';':
                        case '+':
                        case '-':
                        case '*':
                        case '!':
                        case '=':
                        case '&':
                        case '|':
                        case '<':
                            if(!word.equals("")) {
                                sourceFileTokens.add(word);
                                word = "";
                            }
                            sourceFileTokens.add(Character.toString(item));
                            break;

                        default:
                            word += Character.toString(item);
                            break;
                    } //switch
                } //
                if(!word.equals("")) {
                    sourceFileTokens.add(word);
                    word = "";
                }
                line = reader.readLine();
                sourceFileTokens.add("#@#");
            } //while
        } //try

        catch(IOException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        finally {
            try {
                reader.close();
            }
            catch(IOException e) {
                System.err.println("Could not close reader");
                e.printStackTrace();
            }
            catch(Exception e) {
                System.err.println("Could not close reader");
                e.printStackTrace();
            }
        } //finally
    } //readFile

    private void enhanceSourceFileTokens() {
        List<String> enhancedList = new ArrayList<String>();

        //cast to array for easier access
        String[] tokensNow = sourceFileTokens.toArray(new String[0]);

        for(int i = 0; i < tokensNow.length; i++){
            switch(tokensNow[i]) {
                case ":":
                case "<":
                    if(tokensNow[i+1].equals("=")) {
                        String wordToAdd = tokensNow[i] + "=";
                        enhancedList.add(wordToAdd);
                        i++;
                    } //if
                    else {
                        enhancedList.add(tokensNow[i]);
                    }
                    break;
                default:
                    enhancedList.add(tokensNow[i]);
                    break;
            } //switch
        } //for

        sourceFileTokens = enhancedList;
    }

    //method to identify tokens and create a list from them
    private void createTokenList() {
        int lineNo = 0;
        for(String item : sourceFileTokens) {
            switch(item) {
                case ":=":
                    tokens.add(new Token (item,TokenType.ASSIGNEMENT,lineNo));
                    break;
                case "skip":
                    tokens.add(new Token(item,TokenType.SKIP,lineNo));
                    break;
                case ";":
                    tokens.add(new Token(item,TokenType.SEMICOLON,lineNo));
                    break;
                case "(":
                    tokens.add(new Token(item,TokenType.OPAREN,lineNo));
                    break;
                case ")":
                    tokens.add(new Token(item,TokenType.CPAREN,lineNo));
                    break;
                case "{":
                    tokens.add(new Token(item,TokenType.LBRACE,lineNo));
                    break;
                case "}":
                    tokens.add(new Token(item,TokenType.RBRACE,lineNo));
                    break;
                case "if":
                    tokens.add(new Token(item,TokenType.IF,lineNo));
                    break;
                case "then":
                    tokens.add(new Token(item,TokenType.THEN,lineNo));
                    break;
                case "else":
                    tokens.add(new Token(item,TokenType.ELSE,lineNo));
                    break;
                case "while":
                    tokens.add(new Token(item,TokenType.WHILE,lineNo));
                    break;
                case "do":
                    tokens.add(new Token(item,TokenType.DO,lineNo));
                    break;
                case "print":
                    tokens.add(new Token(item,TokenType.PRINT,lineNo));
                    break;
                case "true":
                    tokens.add(new Token(item,TokenType.TRUE,lineNo));
                    break;
                case "false":
                    tokens.add(new Token(item,TokenType.FALSE,lineNo));
                    break;
                case "=":
                    tokens.add(new Token(item,TokenType.EQUAL,lineNo));
                    break;
                case "<":
                    tokens.add(new Token(item,TokenType.LESSTHAN,lineNo));
                    break;
                case "<=":
                    tokens.add(new Token(item,TokenType.LESSTHANOREQUAL,lineNo));
                    break;
                case "!":
                    tokens.add(new Token(item,TokenType.NOT,lineNo));
                    break;
                case "&":
                    tokens.add(new Token(item,TokenType.AND,lineNo));
                    break;
                case "|":
                    tokens.add(new Token(item,TokenType.OR,lineNo));
                    break;
                case "+":
                    tokens.add(new Token(item,TokenType.PLUS,lineNo));
                    break;
                case "-":
                    tokens.add(new Token(item,TokenType.MINUS,lineNo));
                    break;
                case "*":
                    tokens.add(new Token(item,TokenType.MULTIPLICATION,lineNo));
                    break;
                case "#@#":
                    lineNo++;
                    break;

                default:
                    if(isStringNumeric(item))
                        tokens.add(new Token(item,TokenType.INT,lineNo));
                    else
                        tokens.add(new Token(item, TokenType.NAME,lineNo));

                    break;
            } //switch
        } //for
    } //createTokenList

    //helper method for printing the tokens
    private void printTokenList() {
        for(String item : sourceFileTokens) {
            System.out.println(item);
        } //for

        for(Token item : tokens) {
            System.out.println(item);
        }
    } //printTokenList

    private boolean isStringNumeric(String s) {
        //nasty solution but using exceptions
        boolean isNumber = true;
        try {
            int i = Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            isNumber = false;
        }
        return isNumber;
    }

} //Lexer