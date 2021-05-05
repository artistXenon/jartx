package moe.artx.graphic;

import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final public class Color {

    //  Fields
    final static private Pattern hexPattern = Pattern.compile("^#?([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})?$", Pattern.CASE_INSENSITIVE);

    private byte RED = -128, GREEN = -128, BLUE = -128, ALPHA = -1;

    //  Constructor
    //      blank
    public Color() { }
    //      INT
    public Color(int RED, int GREEN, int BLUE, int ALPHA) {
        set(RED, GREEN, BLUE, ALPHA);
    }
    public Color(int RED, int GREEN, int BLUE) {
        set(RED, GREEN, BLUE, 255);
    }
    public Color(int GREY) {
        set(GREY, GREY, GREY, 255);
    }

    //      FLOAT
    public Color(float RED, float GREEN, float BLUE, float ALPHA) {
        set(RED, GREEN, BLUE, ALPHA);
    }
    public Color(float RED, float GREEN, float BLUE) {
        set(RED, GREEN, BLUE, 1f);
    }
    public Color(float GREY) {
        set(GREY, GREY, GREY, 1f);
    }

    //      HEX
    public Color(String hex) {
        if(hex == null)
            throw new InvalidParameterException();

        Matcher matcher = hexPattern.matcher(hex);
        if(!matcher.find())
            throw new InvalidParameterException();

        //  TODO: There is clearly a better way to filter hex input since #fromHex method does the process.
        int maxCount = matcher.group(4) == null ? 3 : 4;
        int currentColor;
        for(int i = 1;i <= maxCount;i++){
            currentColor = (i + 2) % maxCount;
            byte hexUnit = fromHex(matcher.group(i));
            switch(currentColor) {
                case 0:
                    RED = hexUnit;
                    break;
                case 1:
                    GREEN = hexUnit;
                    break;
                case 2:
                    BLUE = hexUnit;
                    break;
                case 3:
                    ALPHA = hexUnit;
                    break;
            }
        }
    }

    //      direct initiation
    private Color(byte RED, byte GREEN, byte BLUE, byte ALPHA) {
        this.RED = RED;
        this.GREEN = GREEN;
        this.BLUE = BLUE;
        this.ALPHA = ALPHA;
    }

    //  byte conversion utility
    private static int toInt(byte _field) {
        return _field & 0xff;
    }
    private static float tofloat(byte _field) {
        return (_field & 0xff) / 255f;
    }
    private static String toHex(byte _field) {
        return Integer.toHexString(toInt(_field));
    }
    private static byte fromInt(int _field) {
        if(_field > 255 || _field < 0)
            throw new InvalidParameterException();
        return (byte) _field;
    }
    private static byte fromFloat(float _field) {
        if(_field > 1 || _field < 0)
            throw new InvalidParameterException();
        return (byte) (_field * 255);
    }
    private static byte fromHex(String _field) {
        if(_field == null || !Pattern.compile("^([0-9a-f]{2})$", Pattern.CASE_INSENSITIVE).matcher(_field).matches())
            throw new InvalidParameterException();
        return fromInt(Integer.parseInt(_field, 16));
    }

    //  GETTER
    //      INT
    public int getR() {
        return toInt(RED);
    }
    public int getG() {
        return toInt(GREEN);
    }
    public int getB() {
        return toInt(BLUE);
    }
    public int getA() {
        return toInt(ALPHA);
    }

    //      FLOAT
    public float getRf() {
        return tofloat(RED);
    }
    public float getGf() {
        return tofloat(GREEN);
    }
    public float getBf() {
        return tofloat(BLUE);
    }
    public float getAf() {
        return tofloat(ALPHA);
    }

    //      HEX
    public String getHex(boolean withAlpha, boolean withSharp) {
        StringBuilder stringBuilder = new StringBuilder();
        if(withSharp) {
            stringBuilder.append("#");
        }
        if(withAlpha) {
            stringBuilder.append(toHex(ALPHA));
        }
        stringBuilder.append(toHex(RED));
        stringBuilder.append(toHex(GREEN));
        stringBuilder.append(toHex(BLUE));
        return stringBuilder.toString();
    }
    //  TODO: getter for each attribute on hex.

    //      ANDROID
    public int getAndroid() {
        return Integer.parseInt(getHex(true, false), 16);
    }

    //  SETTER
    //      INDIVIDUAL - INT/FLOAT/HEX
    public void setR(Object RED) {
        this.RED = objectConvertible(RED);
    }
    public void setG(Object GREEN) {
        this.GREEN = objectConvertible(GREEN);
    }
    public void setB(Object BLUE) {
        this.BLUE = objectConvertible(BLUE);
    }
    public void setA(Object ALPHA) {
        this.ALPHA = objectConvertible(ALPHA);
    }

    //      GROUP - INT
    public void set(int RED, int GREEN, int BLUE, int ALPHA) {
        this.RED = fromInt(RED);
        this.GREEN = fromInt(GREEN);
        this.BLUE = fromInt(BLUE);
        this.ALPHA = fromInt(ALPHA);
    }
    public void set(int RED, int GREEN, int BLUE) {
        set(RED, GREEN, BLUE, this.ALPHA);
    }

    //      GROUP - FLOAT
    public void set(float RED, float GREEN, float BLUE, float ALPHA) {
        this.RED = fromFloat(RED);
        this.GREEN = fromFloat(GREEN);
        this.BLUE = fromFloat(BLUE);
        this.ALPHA = fromFloat(ALPHA);
    }
    public void set(float RED, float GREEN, float BLUE) {
        set(RED, GREEN, BLUE, this.ALPHA);
    }

    private void set(String hex) {
        //      TODO: SETTER GROUP - HEX
    }

    private void set(int android) {
        //      TODO: SETTER GROUP - ANDROID
    }

    public Color copy() {
        return new Color(RED, GREEN, BLUE, ALPHA);
    }

    private static byte objectConvertible(Object object) {
        Object newValue = object instanceof Integer ?  fromInt((int) object) : object instanceof Float ? fromFloat((float) object) : object instanceof String ? fromHex((String) object) : null;
        if(newValue == null)
            throw new InvalidParameterException();
        return (byte) newValue;
    }
}