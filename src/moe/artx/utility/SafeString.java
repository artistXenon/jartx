package moe.artx.utility;

public class SafeString {

    static public int toInt(String source) {
        return toInt(source, 0);
    }

    static public int toInt(String source, int fallback) {
        try {
            return Integer.parseInt(source.trim());
        }
        catch (NumberFormatException ignore) { return fallback; }
    }

    static public float toFloat(String source) {
        return toFloat(source, 0);
    }

    static public float toFloat(String source, float fallback) {
        try {
            return Float.parseFloat(source.trim());
        }
        catch (NumberFormatException ignore) { return fallback; }
    }

    static public double toDouble(String source) {
        return toDouble(source, 0);
    }

    static public double toDouble(String source, double fallback) {
        try {
            return Double.parseDouble(source.trim());
        }
        catch (NumberFormatException ignore) { return fallback; }
    }

    static public long toLong(String source) {
        return toLong(source, 0);
    }

    static public long toLong(String source, long fallback) {
        try {
            return Long.parseLong(source.trim());
        }
        catch (NumberFormatException ignore) { return fallback; }
    }

    static public short toShort(String source) {
        return toShort(source, (short) 0);
    }

    static public short toShort(String source, short fallback) {
        try {
            return Short.parseShort(source.trim());
        }
        catch (NumberFormatException ignore) { return fallback; }
    }

    static public byte toByte(String source) {
        return toByte(source, (byte) 0);
    }

    static public byte toByte(String source, byte fallback) {
        try {
            return Byte.parseByte(source.trim());
        }
        catch (NumberFormatException ignore) { return fallback; }
    }

    static public boolean toBoolean(String source) {
        return toBoolean(source, false);
    }

    static public boolean toBoolean(String source, boolean fallback) {
        try {
            return Boolean.parseBoolean(source.trim());
        }
        catch (NumberFormatException ignore) { return fallback; }
    }

    static public String nullEmpty(String source) {
        return nullSafe(source, "");
    }

    static public String nullSafe(String source, String fallback) {
        if (source != null) return source;
        if (fallback != null) return fallback;
        throw new IllegalArgumentException();
    }
}
