package cloudFileStorage.enums;

import java.text.DecimalFormat;

public enum UserObjectSizeUnits {
    BYTES(" b"),
    KILOBYTES(" kb"),
    MEGABYTES(" mb"),
    GIGABYTES(" gb");
    private final String unit;
    private static final String doubleFormatPattern = "#0.000";

    UserObjectSizeUnits(String unit) {
        this.unit = unit;
    }

    private String getUnit() {
        return this.unit;
    }

    private static String getDoubleFormatPattern() {
        return doubleFormatPattern;
    }

    public static String convertSizeToRequiredUnit(double size) {
        if (size > 0 && size < 1024) {
            return size + BYTES.getUnit();
        } else if (size >= 1024 && size < 1048576) {
            size /= 1024;
            return new DecimalFormat(getDoubleFormatPattern()).format(size) + KILOBYTES.getUnit();
        } else if (size >= 1048576 && size < 1073741824) {
            size /= 1048576;
            return new DecimalFormat(getDoubleFormatPattern()).format(size) + MEGABYTES.getUnit();
        } else {
            size /= 1073741824;
            return new DecimalFormat(getDoubleFormatPattern()).format(size) + GIGABYTES.getUnit();
        }
    }
}
