package com.koi.monkey.code;

import com.koi.monkey.object.Int;

import java.util.HashMap;
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
