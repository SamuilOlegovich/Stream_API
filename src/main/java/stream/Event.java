package stream;

import java.time.LocalDateTime;
import java.util.UUID;



public class Event {
    private LocalDateTime timeTag;
    private String description;
    private UUID id;

    public Event(UUID id, LocalDateTime timeTag, String description) {
        this.description = description;
        this.timeTag = timeTag;
        this.id = id;

        System.out.printf("Generated %s \n", id.toString());
    }
}