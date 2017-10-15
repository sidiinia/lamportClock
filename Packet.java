import java.io.Serializable;

public class Packet implements Serializable {

    private final String message;

    private final int processId;

    /**
     * Piggyback time from sender process.
     */
    private final int time;

    public Packet(String message, int processId, int time) {
        this.message = message;
        this.processId = processId;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public int getProcessId() {
        return processId;
    }

    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        return String.format("Packet [message=%s, receiving processId=%s, piggyback time=%s]", message, processId, time);
    }
}