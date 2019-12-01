package com.koi.monkey.code;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
/**
 * @author whuang
 * @date 2019/11/20
 */
public class CodeTest {
    @Test
    public void testMake() {
        class Struct {
            byte op;
            int[] operands;
            byte[] expected;

            public Struct(byte op, int[] operands, byte[] expected) {
                this.op = op;
                this.operands = operands;
                this.expected = expected;
            }
        }
        Struct[] tests = new Struct[]{
             new Struct(Code.OpAdd,new int[]{},new byte[]{
                     Code.OpAdd
             })
        };
        for (Struct tt : tests) {
            byte[] instruction = Code.make(tt.op,tt.operands);
            assertThat(instruction.length).isEqualTo(tt.expected.length);
            for (int i = 0; i < tt.expected.length; i++) {
                assertThat(instruction[i]).isEqualTo(tt.expected[i]);
            }
        }
    }
}