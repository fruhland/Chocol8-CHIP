package one.ruhland.chocol8.swing;

public class ValueFormatter {
    private static final char[] highMetricTable = {
            0,
            'K',
            'M',
            'G',
            'T',
            'P',
            'E'
    };

    private static final char[] lowMetricTable = {
            0,
            'm',
            'u',
            'n',
            'p',
            'f',
            'a'
    };

    private static String formatHighValue(final double value, final String unit) {
        double formattedValue = value;

        int counter = 0;
        while(formattedValue >= 1000 && formattedValue != 0 && counter < highMetricTable.length - 1) {
            formattedValue /= 1000;
            counter++;
        }

        return String.format("%.3f %c%s", formattedValue, highMetricTable[counter], unit);
    }

    private static String formatLowValue(final double value, final String unit) {
        double formattedValue = value;

        int counter = 0;
        while(formattedValue < 1 && formattedValue != 0 && counter < lowMetricTable.length - 1) {
            formattedValue *= 1000;
            counter++;
        }

        return String.format("%.3f %c%s", formattedValue, lowMetricTable[counter], unit);
    }

    static String formatValue(final double value, final String unit) {
        if(value >= 1) {
            return formatHighValue(value, unit);
        } else {
            return formatLowValue(value, unit);
        }
    }
}
