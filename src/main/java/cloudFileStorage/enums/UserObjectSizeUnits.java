package cloudFileStorage.enums;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public enum UserObjectSizeUnits {
    BYTES(" b"),
    KILOBYTES(" kb"),
    MEGABYTES(" mb"),
    GIGABYTES(" gb");
    private final String unit;
    private static String doubleFormatPattern = "#,###.##";
    private static DecimalFormatSymbols dfs = new DecimalFormatSymbols();

    UserObjectSizeUnits(String unit) {
        this.unit = unit;
    }

    private String getUnit() {
        return this.unit;
    }


    public static String convertSizeToRequiredUnit(double size) {
        dfs.setDecimalSeparator('.');
        dfs.setGroupingSeparator(',');
        if (size >= 0 && size < 1024) {
            return size + BYTES.getUnit();
        } else if (size >= 1024 && size < 1048576) {
            size /= 1024;
            return new DecimalFormat(doubleFormatPattern, dfs).format(size) + KILOBYTES.getUnit();
        } else if (size >= 1048576 && size < 1073741824) {
            size /= 1048576;
            return new DecimalFormat(doubleFormatPattern, dfs).format(size) + MEGABYTES.getUnit();
        } else {
            size /= 1073741824;
            return new DecimalFormat(doubleFormatPattern, dfs).format(size) + GIGABYTES.getUnit();
        }
    }
}
