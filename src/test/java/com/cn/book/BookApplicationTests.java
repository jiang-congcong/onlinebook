package com.cn.book;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookApplicationTests {

    @Test
    void contextLoads() {
        String a = "2020-12-20 10:10:11";
        String b = "2021-12-20 10:10:11";
        System.out.println(a.compareTo(b));
    }

}
