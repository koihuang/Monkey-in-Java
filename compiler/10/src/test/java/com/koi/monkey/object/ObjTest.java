package com.koi.monkey.object;

import org.junit.Test;

import static org.junit.Assert.*;
import static com.google.common.truth.Truth.assertThat;
/**
 * @author whuang
 * @date 2019/11/19
 */
public class ObjTest {

    @Test
    public void testStringHashKey() {
        Str hello1 = new Str("Hello World");
        Str hello2 = new Str("Hello World");
        Str diff1 = new Str("My name is johnny");
        Str diff2 = new Str("My name is johnny");
        assertThat(hello1.hashKey()).isEqualTo(hello2.hashKey());
        assertThat(diff1.hashKey()).isEqualTo(diff2.hashKey());
        assertThat(hello1.hashKey()).isNotEqualTo(diff1.hashKey());

    }
}