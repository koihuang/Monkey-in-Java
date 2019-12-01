package com.koi.monkey.code;

import com.koi.monkey.object.Int;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author whuang
 * @date 2019/11/20
 */
public class Code {

    public static byte iota = 0;
    public static final byte OpConstant = 1;
    public static final byte OpAdd = 2;
    public static final byte OpPop = 3;
    public static final byte OpSub = 4;
    public static final byte OpMul = 5;
    public static final byte OpDiv = 6;
    public static final byte OpTrue = 7;
    public static final byte OpFalse = 8;
    public static final byte OpEqual = 9;
    public static final byte OpNotEqual = 10;
    public static final byte OpGreaterThan = 11;
    public static final byte OpMinus = 12;
    public static final byte OpBang = 13;
    public static final byte OpJumpNotTruthy = 14;
    public static final byte OpJump = 15;
    public static final byte OpNull = 16;
    public static final byte OpGetGlobal = 17;
    public static final byte OpSetGlobal = 18;
    public static final byte OpArray = 19;
    public static final byte OpHash = 20;
    public static final byte OpIndex = 21;
    public static final byte OpCall = 22;
    public static final byte OpReturnValue = 23;
    public static final byte OpReturn = 24;
    public static final byte OpGetLocal = 25;
    public static final byte OpSetLocal = 26;
    public static final byte OpGetBuiltin = 27;
    public static final byte OpClosure = 28;
    public static final byte OpGetFree = 29;

    public static Map<Byte,Definition> definitions = new HashMap<>();

    static {
        definitions.put(OpConstant,new Definition("OpConstant",1));
        definitions.put(OpAdd,new Definition("OpAdd",0));
        definitions.put(OpPop,new Definition("OpPop",0));
        definitions.put(OpSub,new Definition("OpSub",0));
        definitions.put(OpMul,new Definition("OpMul",0));
        definitions.put(OpDiv,new Definition("OpDiv",0));
        definitions.put(OpTrue,new Definition("OpTrue",0));
        definitions.put(OpFalse,new Definition("OpFalse",0));
        definitions.put(OpEqual,new Definition("OpEqual",0));
        definitions.put(OpNotEqual,new Definition("OpNotEqual",0));
        definitions.put(OpGreaterThan,new Definition("OpGreaterThan",0));
        definitions.put(OpMinus,new Definition("OpMinus",0));
        definitions.put(OpBang,new Definition("OpBang",0));
        definitions.put(OpJumpNotTruthy,new Definition("OpJumpNotTruthy",1));
        definitions.put(OpJump,new Definition("OpJump",1));
        definitions.put(OpNull,new Definition("OpNull",0));
        definitions.put(OpGetGlobal,new Definition("OpGetGlobal",1));
        definitions.put(OpSetGlobal,new Definition("OpSetGlobal",1));
        definitions.put(OpArray,new Definition("OpArray",1));
        definitions.put(OpHash,new Definition("OpHash",1));
        definitions.put(OpIndex,new Definition("OpIndex",0));
        definitions.put(OpCall,new Definition("OpCall",1));
        definitions.put(OpReturnValue,new Definition("OpReturnValue",0));
        definitions.put(OpReturn,new Definition("OpReturn",0));
        definitions.put(OpGetLocal,new Definition("OpGetLocal",1));
        definitions.put(OpSetLocal,new Definition("OpSetLocal",1));
        definitions.put(OpGetBuiltin,new Definition("OpGetBuiltin",1));
        definitions.put(OpClosure,new Definition("OpClosure",2));
        definitions.put(OpGetFree,new Definition("OpGetFree",1));
    }

    public static String string(List<Byte> instructions) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < instructions.size()){
            byte op = instructions.get(i);
            Definition def = definitions.get(op);
            sb.append(def.name + "  ");
            i++;
            for (int j = 0; j < def.operandNum; j++) {
                byte oprand = instructions.get(i+j);
                sb.append(oprand);
                i++;
            }

            sb.append("\r\n");
        }
        return sb.toString();
    }
    public static byte[] make(byte op, int...operands) {
        byte[] instruction = new byte[operands.length+1];
        instruction[0] = op;
        for (int i = 0; i < operands.length; i++) {
            instruction[i+1] = (byte) operands[i];
        }
        return instruction;
    }

    public static byte[] int2ByteArr(long val) {
        byte[] byteArr = new byte[2];
        byteArr[0] = (byte) (val  & 0xFF);
        byteArr[1] = (byte) ((val  & 0xFFFF) );
        return byteArr;
    }

    public static void main(String[] args) {
        System.out.println((byte) 255);
        for (byte b : int2ByteArr(Integer.toUnsignedLong(65534))) {
            System.out.println(b);
        }

    }
}
