import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

// this custom formatter formats parts of a log record to a single line
class MyLogFormatter extends Formatter {
    Integer nodeId;

    MyLogFormatter(Integer nodeId)
    {
        this.nodeId = nodeId;
    }

    // this method is called for every log records
    public String format(LogRecord rec) {
        StringBuffer buf = new StringBuffer(1000);
        buf.append("Node Id " + nodeId.toString() + "---->");
        buf.append(calcDate(rec.getMillis()));
        buf.append(" ");
        buf.append(formatMessage(rec));
        buf.append("\n");
        return buf.toString();
    }

    private String calcDate(long milliseconds) {
        SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultDate = new Date(milliseconds);
        return date_format.format(resultDate);
    }
}