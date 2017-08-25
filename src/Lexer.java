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
        printTokenList();
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
                    } //switch
                } //for
                line = reader.readLine();
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
        for(String item : sourceFileTokens) {
            switch(item) {
                case ":=":
                    tokens.add(new Token (item,TokenType.ASSIGNEMENT));
                    break;
                case "skip":
                    tokens.add(new Token(item,TokenType.SKIP));
                    break;
                case ";":
                    tokens.add(new Token(item,TokenType.SEMICOLON));
                    break;
                case "(":
                    tokens.add(new Token(item,TokenType.OPAREN));
                    break;
                case ")":
                    tokens.add(new Token(item,TokenType.CPAREN));
                    break;
                case "{":
                    tokens.add(new Token(item,TokenType.LBRACE));
                    break;
                case "}":
                    tokens.add(new Token(item,TokenType.RBRACE));
                    break;
                case "if":
                    tokens.add(new Token(item,TokenType.IF));
                    break;
                case "then":
                    tokens.add(new Token(item,TokenType.THEN));
                    break;
                case "else":
                    tokens.add(new Token(item,TokenType.ELSE));
                    break;
                case "while":
                    tokens.add(new Token(item,TokenType.WHILE));
                    break;
                case "do":
                    tokens.add(new Token(item,TokenType.DO));
                    break;
                case "print":
                    tokens.add(new Token(item,TokenType.PRINT));
                    break;
                case "true":
                    tokens.add(new Token(item,TokenType.TRUE));
                    break;
                case "false":
                    tokens.add(new Token(item,TokenType.FALSE));
                    break;
                case "=":
                    tokens.add(new Token(item,TokenType.EQUAL));
                    break;
                case "<":
                    tokens.add(new Token(item,TokenType.LESSTHAN));
                    break;
                case "<=":
                    tokens.add(new Token(item,TokenType.LESSTHANOREQUAL));
                    break;
                case "!":
                    tokens.add(new Token(item,TokenType.NOT));
                    break;
                case "&":
                    tokens.add(new Token(item,TokenType.AND));
                    break;
                case "|":
                    tokens.add(new Token(item,TokenType.OR));
                    break;
                case "+":
                    tokens.add(new Token(item,TokenType.PLUS));
                    break;
                case "-":
                    tokens.add(new Token(item,TokenType.MINUS));
                    break;
                case "*":
                    tokens.add(new Token(item,TokenType.MULTIPLICATION));
                    break;

                default:
                    if(isStringNumeric(item))
                        tokens.add(new Token(item,TokenType.INT));
                    else
                        tokens.add(new Token(item, TokenType.NAME));

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