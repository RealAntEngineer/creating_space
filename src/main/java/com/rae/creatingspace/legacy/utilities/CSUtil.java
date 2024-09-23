package com.rae.creatingspace.legacy.utilities;

import java.math.BigDecimal;

public class CSUtil {
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //TODO use String.format()
    public static String scientificNbrFormatting(Float toRound, int digit) {
        try {
            return scientificNbrFormatting(BigDecimal.valueOf(toRound), digit);
        } catch (NumberFormatException exception) {
            return "NaN";
        }

    }
    public static String scientificNbrFormatting(BigDecimal toRound, int digit) {
        if (toRound.floatValue() == 0) return "0";
        BigDecimal rounded = toRound;
        int nbrOf10Pow = 0;
        while ((rounded.floatValue() > 1000 || rounded.floatValue() < 0.1) && (-3 < nbrOf10Pow && nbrOf10Pow < 6)) {
            if (rounded.floatValue() > 1) {
                rounded = rounded.movePointLeft(3);
                nbrOf10Pow += 3;
            } else if (rounded.floatValue() < 1) {
                rounded = rounded.movePointRight(3);
                nbrOf10Pow -= 3;
            }
        }
        String unitCoef;
        if (nbrOf10Pow >= 6) {
            unitCoef = "M";
        } else if (nbrOf10Pow >= 3) {
            unitCoef = "k";
        } else if (nbrOf10Pow >= 0) {
            unitCoef = "";
        } else {
            unitCoef = "m";
        }
        String floatString = String.valueOf(rounded.floatValue());
        return floatString.subSequence(0, Math.min(floatString.length(), rounded.floatValue() < Math.pow(10, digit - 1) ? digit + 1 : digit)) + unitCoef;
    }
}