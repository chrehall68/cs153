/**
 * <h1>Instruction</h1>
 *
 * <p>Jasmin instructions.</p>
 *
 * <p>Copyright (c) 2024 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
package backend.compiler;

public enum Instruction
{
    // Load constant
    ICONST_0(1), ICONST_1(1), ICONST_2(1), ICONST_3(1), 
    ICONST_4(1), ICONST_5(1), ICONST_M1(1),
    DCONST_0(2), DCONST_1(2), DCONST_2(2), ACONST_NULL(1),
    BIPUSH(1), SIPUSH(1), LDC(1), LDC2_W(2),

    // Load value or address
    ILOAD_0(1), ILOAD_1(1), ILOAD_2(1), ILOAD_3(1),
    DLOAD_0(2), DLOAD_1(2), DLOAD_2(2), DLOAD_3(2),
    ALOAD_0(1), ALOAD_1(1), ALOAD_2(1), ALOAD_3(1),
    LLOAD_0(2), LLOAD_1(2), LLOAD_2(2), LLOAD_3(2),
    ILOAD(1),   DLOAD(2),   ALOAD(1),
    GETSTATIC(1), GETFIELD(0),

    // Store value or address
    ISTORE_0(-1),  ISTORE_1(-1), ISTORE_2(-1), ISTORE_3(-1),
    DSTORE_0(-2),  DSTORE_1(-2), DSTORE_2(-2), DSTORE_3(-2),
    ASTORE_0(-1),  ASTORE_1(-1), ASTORE_2(-1), ASTORE_3(-1),
    LSTORE_0(-2),  LSTORE_1(-2), LSTORE_2(-2), LSTORE_3(-2),
    ISTORE(-1),    DSTORE(-2),   ASTORE(-1),
    PUTSTATIC(-1), PUTFIELD(-2),

    // Operand stack
    POP(-1), SWAP(0), DUP(1), DUP_X1(1), DUP_X2(1),

    // Arithmetic and logical
    IADD(-1), DADD(-2), ISUB(-1), DSUB(-2), IMUL(-1), DMUL(-2),
    IDIV(-1), DDIV(-2), IREM(-1), DREM(-2), INEG(0),  DNEG(0),
    IINC(0),  IAND(-1), IOR(-1),  IXOR(-1),

    // Type conversion and checking
    I2C(0), I2D(1), D2I(-1),
    CHECKCAST(0),

    // Objects and arrays
    NEW(1), NEWARRAY(0), ANEWARRAY(0), MULTIANEWARRAY(0),
    IALOAD(-1), DALOAD(0), BALOAD(-1), CALOAD(-1), AALOAD(-1),
    IASTORE(-3), DASTORE(-4), BASTORE(-3), CASTORE(-3), AASTORE(-3),

    // Compare and branch
    IFEQ(-1), IFNE(-1), IFLT(-1), IFLE(-1), IFGT(-1), IFGE(-1),
    IF_ICMPEQ(-2), IF_ICMPNE(-2), IF_ICMPLT(-2), 
    IF_ICMPLE(-2), IF_ICMPGT(-2), IF_ICMPGE(-2),
    DCMPG(-3), GOTO(0), LOOKUPSWITCH(-1),

    // Call and return
    INVOKESTATIC(0), INVOKESPECIAL(0), 
    INVOKEVIRTUAL(0), INVOKENONVIRTUAL(0),
    RETURN(0), IRETURN(-1), DRETURN(-2), ARETURN(-1),

    // No operation
    NOP(0);
    
    public int stackUse;
    
    Instruction(int stackUse) { this.stackUse = stackUse; }

    /**
     * Generate the instruction text that is emitted.
     * @return the text.
     */
    public String toString() { return super.toString().toLowerCase(); }
}
