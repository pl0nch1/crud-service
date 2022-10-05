package models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ticket {
    private int ticketId;
    private String title;
    private String description;
    private Status currentStatus;
    private short priority;
    private Queue queue;
}
