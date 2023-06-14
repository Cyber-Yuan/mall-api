package org.csu.api.util;

import java.math.BigDecimal;

public class BigDecimalUtil {
    private BigDecimalUtil(){}

    public static BigDecimal add(double d1, double d2) {
        return new BigDecimal(Double.toString(d1)).add(new BigDecimal(Double.toString(d2)));
    }

    public static BigDecimal multiply(double d1, double d2) {
        return new BigDecimal(Double.toString(d1)).multiply(new BigDecimal(Double.toString(d2)));
    }

    //to do: substract divide
}
