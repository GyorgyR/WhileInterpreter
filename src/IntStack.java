/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.18.
--------------------------------------------------------------------------------
    Description: A custom implementation of a stack. Array based.
*/

public class IntStack {

    private int[] memSpace;
    private int spaceCounter;

    //constructor
    public IntStack() {
        memSpace = new int[10];
        spaceCounter = 0;
    } //constructor

    //constructor
    public IntStack(int size) {
        memSpace = new int[size];
        spaceCounter = 0;
    } //constructor

    public void push(int value) {
        if(spaceCounter == memSpace.length) {
            //deep copy
            int[] newArr = new int[memSpace.length];
            for(int i = 0; i < memSpace.length; i++)
                newArr[i] = memSpace[i];

            memSpace = newArr;
        } //if

        memSpace[spaceCounter] = value;
        spaceCounter++;
    } //push

    public int pop() {
        spaceCounter--;
        return memSpace[spaceCounter];
    } //pop
} //IntStack