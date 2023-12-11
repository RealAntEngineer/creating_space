package com.rae.creatingspace.utilities;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class CSUtil {

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String scientificNbrFormatting(Float toRound, int digit) {
        return scientificNbrFormatting(BigDecimal.valueOf(toRound),digit);
    }

    public static String scientificNbrFormatting(Integer toRound, int digit) {
        return scientificNbrFormatting((float) toRound, digit);
    }
    //using BigDecimal is a better way

    //it's still not prefect
    public static String scientificNbrFormatting(BigDecimal toRound, int digit) {
        if (toRound.floatValue() == 0) return "0";
        digit = Math.max(0, Math.abs(digit - 1));
        int nbrOf10pow = (int) Math.log10(toRound.floatValue());
        if (toRound.floatValue() < 1) {
            nbrOf10pow--;
        }
        BigDecimal powOf10Value = new BigDecimal(BigInteger.ONE,-nbrOf10pow);//+1 to obtain a nbr bwn 0.1 and 0.9 ?
        String unitCoef;
        int roundedPow;
        if (nbrOf10pow >= 6) {
            unitCoef = "M";
            roundedPow = nbrOf10pow - 6;
        } else if (nbrOf10pow >= 3) {
            unitCoef = "k";
            roundedPow = nbrOf10pow - 3;
        } else if (nbrOf10pow >= 0) {
            unitCoef = "";
            roundedPow = nbrOf10pow;
        } else {
            unitCoef = "m";
            roundedPow = 3 + nbrOf10pow;
        }
        //rounding
        BigDecimal to1Value = toRound.divide(powOf10Value,RoundingMode.HALF_UP);
        BigDecimal roundedValue = to1Value.setScale(digit,RoundingMode.HALF_UP);
        BigDecimal powOf10Remaining = new BigDecimal(BigInteger.ONE,-roundedPow);
        BigDecimal finalValue = roundedValue.multiply(powOf10Remaining);
        return finalValue.floatValue()+unitCoef;
    }
}
