package pw.komarov.aeonpay.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public final class RandomUtils {
    private static final Random random = new Random();

    public static long nextLongFixedDigits(int digits) {
        if (digits < 1 || digits > 20)
            throw new IllegalArgumentException("nextLongFixedDigits(): digits must be greater than 0 and less 21");

        StringBuilder sb = new StringBuilder().append(random.nextInt(8) + 1);
        for (int i = 2; i <= digits; i++)
            sb.append(random.nextInt(9));

        return Long.parseLong(sb.toString());
    }

    public static long nextLongMaxDigits(int maxDigits) {
        if (maxDigits < 1 || maxDigits > 20)
            throw new IllegalArgumentException("nextLongMaxDigits(): maxDigits must be greater than 0 and less 21");

        return nextLongFixedDigits(random.nextInt(maxDigits - 1) + 1);
    }

    public static BigDecimal nextAmount(long max) {
        long dec = Math.abs(random.nextLong());
        dec = (dec >= max) ? max - 1 : dec;
        return BigDecimal.valueOf(dec)
                .add(BigDecimal.valueOf(random.nextFloat()))
                .setScale(2, RoundingMode.FLOOR);
    }

    public static BigDecimal nextAmount() {
        return nextAmount(Long.MAX_VALUE);
    }

    public static BigDecimal nextAmountFixedDecimalDigits(int digits) {
        return nextAmount(nextLongFixedDigits(digits));
    }

    public static BigDecimal nextAmountMaxDecimalDigits(int maxDigits) {
        return nextAmount(nextLongMaxDigits(maxDigits));
    }
}
