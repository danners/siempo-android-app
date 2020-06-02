package co.siempo.phone.log;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Decide the format of log will be written to the file.
 * <p>
 * Created by hui.yang on 2014/11/16.
 */
public abstract class LogFormatter {
    /**
     * format the log.
     *
     * @param level
     * @param tag
     * @param msg
     * @return
     */
    public abstract String format(LEVEL level, String tag, String msg, Throwable tr);

    public enum LEVEL {
        VERBOSE(2, "V"),
        DEBUG(3, "D"),
        INFO(4, "I"),
        WARN(5, "W"),
        ERROR(6, "E"),
        ASSERT(7, "A");

        final String levelString;
        final int level;

        //Supress default constructor for noninstantiability
        LEVEL() {
            throw new AssertionError();
        }

        LEVEL(int level, String levelString) {
            this.level = level;
            this.levelString = levelString;
        }

    }

    /**
     * Eclipse Style
     */
    public static class EclipseFormatter extends LogFormatter {
        private final SimpleDateFormat formatter;

        public EclipseFormatter() {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.US);
        }

        @Override
        public String format(LEVEL level, String tag, String msg, Throwable tr) {
            if (TextUtils.isEmpty(msg)) {
                return "";
            }

            StringBuilder buffer = new StringBuilder();
            buffer.append(formatter.format(System.currentTimeMillis()));
            buffer.append("\t");
            buffer.append(msg);
            if (tr != null) {
                buffer.append(System.getProperty("line.separator"));
                buffer.append(android.util.Log.getStackTraceString(tr));
            }
            buffer.append("\n");

            return buffer.toString();
        }
    }
}
