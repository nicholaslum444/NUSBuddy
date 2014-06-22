/*
 * ISO8601.java
 *
 * This file is part of the specification for the
 * Java Content Repository API
 */
package helpers.com.nick.nusbuddy;

import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;

/**
 * The <code>ISO8601</code> utility class provides helper methods
 * to deal with date/time formatting using a specific ISO8601-compliant
 * format (see (<a href="http://www.w3.org/TR/NOTE-datetime">ISO8601</a>)).
 * <p>
 * Currently we only support the format 'yyyy-mm-ddThh:mm:ss.SSSZTD', which
 * includes the complete date plus hours, minutes, seconds and a decimal
 * fraction of a second. The time zone part, ZTD, is either Z for Zulu, i.e.
 * UTC, or an offset from UTC in the form of +hh:mm or -hh:mm."
 *
 * @author sguggisb
 */
public final class ISO8601 {
    /** used to fomat time zone offset */
    private static DecimalFormat xxFormat = new DecimalFormat("00");

    /**
     * specific ISO8601-compliant format string (without time zone information)
     */
    private static String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /**
     * Parses a ISO8601-compliant date/time string.
     * @param text the date/time string to be parsed
     * @return a <code>Calendar</code>, or <code>null</code>
     * if the input could not be parsed
     */
    public static Calendar parse(String text) {
        // parse time zone designator (Z or +00:00 or -00:00)
        // and build time zone id (GMT or GMT+00:00 or GMT-00:00)
        String tzID = "GMT";	// Zulu, i.e. UTC/GMT (default)
        int tzPos = text.indexOf('Z');
        if (tzPos == -1) {
            // not Zulu, try +
            tzPos = text.indexOf('+');
            if (tzPos == -1) {
                // not +, try -, but remember it might be used within first
                // 8 charaters for separating year, month and day, yyyy-mm-dd
                tzPos = text.indexOf('-', 8);
            }
            if (tzPos == -1) {
                // no time zone specified, assume Zulu
            } else {
                // offset to UTC specified in the format +00:00/-00:00
                tzID += text.substring(tzPos);
                text = text.substring(0, tzPos);
            }
        } else {
            // Zulu, i.e. UTC/GMT
            text = text.substring(0, tzPos);
        }

        TimeZone tz = TimeZone.getTimeZone(tzID);
        SimpleDateFormat format = new SimpleDateFormat(ISO_FORMAT, Locale.ENGLISH);
        format.setLenient(false);
        format.setTimeZone(tz);
        Date date = format.parse(text, new ParsePosition(0));
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance(tz);
        cal.setTime(date);
        return cal;
    }

    /**
     * Formats a <code>Calendar</code> value into a ISO8601-compliant
     * date/time string.
     * @param cal the time value to be formatted into a date/time string.
     * @return the formatted date/time string.
     */
    public static String format(Calendar cal) {
        SimpleDateFormat format =
                new SimpleDateFormat(ISO_FORMAT, Locale.ENGLISH);
        TimeZone tz = cal.getTimeZone();
        format.setTimeZone(tz);

        StringBuffer tzd = new StringBuffer("Z");
        int offset = tz.getRawOffset();
        if (offset != 0) {
            int hours = Math.abs((offset / (60 * 1000)) / 60);
            int minutes = Math.abs((offset / (60 * 1000)) % 60);
            tzd.append(offset < 0 ? "-" : "+");
            tzd.append(xxFormat.format(hours));
            tzd.append(":");
            tzd.append(xxFormat.format(minutes));
        }
        return format.format(cal.getTime()) + tzd.toString();
    }
}