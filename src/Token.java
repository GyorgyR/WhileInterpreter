/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.17.
--------------------------------------------------------------------------------
    Descrpition: Data structure for tokens.
*/

public class Token {

    public final String tokenValue;
    public final TokenType tokenType;
    public final int lineNo;

    //constructor
    public Token (String _tokenValue, TokenType _tokenType, int _lineNo) {
        //assignements
        tokenValue = _tokenValue;
        tokenType = _tokenType;
        lineNo = _lineNo;
    }

    @Override
    public String toString() {
        return "(\""+tokenValue+"\",\""+tokenType.name()+"\","+lineNo+")";
    }

} //Tokens
