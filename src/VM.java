/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.18.
--------------------------------------------------------------------------------
    Description: Virtual machine. Stack based, very simple.
*/

/*
    ==========================================================================
    --------------------------INSTRUCTION SET---------------------------------
    ==========================================================================

    -iconst N : push N to the top of the stack.

    -isave x  : add the top of the stack to the varPool at position x.

    -iload x  : push the number at position x in varPool to the stack.

    -iadd     : add the two most top values of the stack and push the value.

    -isub     : subtract the top most value from the one underneath.

    -imul     : multiply the two top values.

    -jmp N    : jump to instruction at N.

    -jmpg N   : jump to instruction at N if top of the stack is bigger than 
                 the one underneath.

    -jmpge N  : jump to instruction at N if top <= top-1.

    -print    : print the value at the top of the stack.

    -halt     : end program.

    N = number        x = variable
*/

public class VM {

    //program counter
    private int pc;

    //memory space for variables
    private int[] varPool;

    //the program in bytecode
    private byte[] byteCode;

    //names of the bytes in the bytecode
    byte iconst = 0,
         isave = 1,
         iload = 2,
         iadd = 3, 
         isub = 4,
         imul = 5,
         jmp = 6,
         jmpg = 7,
         jmpge = 8,
         print = 9,
         halt = 10;


    //constructor
    public VM(int varCount, byte[] theProgram) {

        //initializitions
        pc = 0;
        varPool = new int[varCount];
        byteCode = theProgram;
    } //constructor


    public void start() {

    } //start
} //VM