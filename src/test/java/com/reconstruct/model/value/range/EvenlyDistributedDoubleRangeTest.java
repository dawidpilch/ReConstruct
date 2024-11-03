package com.reconstruct.model.value.range;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvenlyDistributedDoubleRangeTest {

    @Test
    public void test() {
        List<Double> values = new EvenlyDistributedDoubleRange(0, 10, 10).values();
        for (Double d :
                values) {
            System.out.println(d);
        }
    }

}