/*
    Project: WhileInterpreter
    Author: Gyorgy Rethy
    Date: 2017.08.22.
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

    -jmp N    : jump to instruction at N relative to position.

    -jmpg N   : jump to instruction at N relative to position if top < top-1.

    -jmpn N   : jump to instruction at N relative to pc if top > top-1

    -jmpge N  : jump to instruction at N relative to position if top <= top-1.

    -jmpeq N  : jump to instrucion at N relative to position if top == top-1.

    -jmpne N : jump to instruction at N relative to pc if top != top-1.

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
    //(which is actually int for simplicity)
    private int[] byteCode;

    private IntStack theStack;

    //names of the bytes in the bytecode
    public final static int iconst = 0,
                            isave = 1,
                            iload = 2,
                            iadd = 3, 
                            isub = 4,
                            imul = 5,
                            jmp = 6,
                            jmpg = 7,
                            jmpn = 8,
                            jmpge = 9,
                            jmpen = 10,
                            jmpeq = 11,
                            jmpne = 12,
                            print = 13,
                            halt = 14;


    //constructor
    public VM(int varCount, int[] theProgram) {

        //initializitions
        pc = 0;
        varPool = new int[varCount];
        byteCode = theProgram;
        theStack = new IntStack();
        printInstructions();
    } //constructor


    public void start() {
        boolean isRunning = true;

        System.out.println("Output:");
        while(isRunning) {
            //System.out.println(byteCode[pc]);
            int top;
            int underneath;
            switch(byteCode[pc]) {
                case iconst:
                    theStack.push(byteCode[pc+1]);
                    pc++;
                    break;
                case isave:
                    varPool[byteCode[pc+1]] = theStack.pop();
                    pc++;
                    break;
                case iload:
                    theStack.push(varPool[byteCode[pc+1]]);
                    pc++;
                    break;
                case iadd:
                    top = theStack.pop();
                    underneath = theStack.pop();
                    theStack.push(underneath+top);
                    break;
                case isub:
                    top = theStack.pop();
                    underneath = theStack.pop();
                    theStack.push(underneath-top);
                    break;
                case imul:
                    top = theStack.pop();
                    underneath = theStack.pop();
                    theStack.push(underneath*top);
                    break;
                case jmp:
                    pc += byteCode[pc+1] - 1;
                    break;
                case jmpg:
                    top = theStack.pop();
                    underneath = theStack.pop();
                    if(top < underneath)
                        pc += byteCode[pc+1]-1;
                    else
                        pc++;
                    break;
                case jmpn:
                    top = theStack.pop();
                    underneath = theStack.pop();
                    if(top > underneath)
                        pc += byteCode[pc+1]-1;
                    else
                        pc++;
                    break;
                case jmpge:
                    top = theStack.pop();
                    underneath = theStack.pop();
                    if(top <= underneath)
                        pc += byteCode[pc+1]-1;
                    else
                        pc++;
                    break;
                case jmpen:
                    top = theStack.pop();
                    underneath = theStack.pop();
                    if(top >= underneath)
                        pc += byteCode[pc+1]-1;
                    else
                        pc++;
                    break;
                case jmpeq:
                    top = theStack.pop();
                    underneath = theStack.pop();
                    if(top == underneath)
                        pc += byteCode[pc+1]-1;
                    else
                        pc++;
                    break;
                case jmpne:
                    top = theStack.pop();
                    underneath = theStack.pop();
                    if(top != underneath)
                        pc += byteCode[pc+1]-1;
                    else
                        pc++;
                    break;
                case print:
                    int numberToPrint = theStack.pop();
                    System.out.println(numberToPrint);
                    break;
                case halt:
                    isRunning = false;
                    break;
                default:
                    System.out.println("There is an error with the bytecode: "+ byteCode[pc]);
                    break;
            } //switch
            pc++;
            //System.out.println(pc);
        } //while

    } //start

    private void printInstructions() {
        String name;
        for(int i = 0; i < byteCode.length; i++) {
            switch(byteCode[i]) {
                case iconst:
                    name = i+": iconst \t" + byteCode[i+1];
                    i++;
                    break;
                case isave:
                    name = i+": isave \t" + byteCode[i+1];
                    i++;
                    break;
                case iload:
                    name = i+": iload \t" + byteCode[i+1];
                    i++;
                    break;
                case iadd:
                    name = i+": iadd";
                    break;
                case isub:
                    name = i+": isub";
                    break;
                case imul:
                    name = i+": imul";
                    break;
                case jmp:
                    name = i+": jmp \t" + byteCode[i+1];
                    i++;
                    break;
                case jmpg:
                    name = i+": jmpg \t" + byteCode[i+1];
                    i++;
                    break;
                case jmpn:
                    name = i + ": jmpn \t" + byteCode[i+1];
                    i++;
                    break;
                case jmpge:
                    name = i+": jmpge \t" + byteCode[i+1];
                    i++;
                    break;
                case jmpen:
                    name = i+": jmpen \t" + byteCode[i+1];
                    i++;
                    break;
                case jmpeq:
                    name = i+": jmpeq \t" + byteCode[i+1];
                    i++;
                    break;
                case jmpne:
                    name = i+": jmpne \t" + byteCode[i+1];
                    i++;
                    break;
                case print:
                    name = i+": print";
                    break;
                case halt:
                    name = i+": halt";
                    break;
                default:
                    name = i+": Bad code: " + byteCode[i];
                    break;
            } //switch

            System.out.println(name);
        } //for
        System.out.println();
    } //printInstructions
} //VM