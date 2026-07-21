package ir.ac.aut.secondhand.frontend.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class MoneyUtils {
    private static final NumberFormat FORMAT = NumberFormat.getIntegerInstance(Locale.US);

    private MoneyUtils() {
    }

    public static String format(long value) {
        return FORMAT.format(value);
    }
}
