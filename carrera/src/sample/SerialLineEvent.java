package sample;

import javafx.event.Event;
import javafx.event.EventType;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by tuxburner on 4/6/14.
 */
public class SerialLineEvent extends Event {

    /**
     * EventType that a serial line was readed
     */
    public static final EventType<SerialLineEvent> SERIAL_LINE_READ = new EventType(ANY, "SERIAL_LINE_READ");

    /**
     * The message that was readed by the serial communication
     */
    public String[] message;

    public SerialLineEvent(final String message) {
        this(SERIAL_LINE_READ);
        this.message = StringUtils.split(message,',');
    }

    public SerialLineEvent(EventType<? extends Event> arg0) {
        super(arg0);
    }
}
