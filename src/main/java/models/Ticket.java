package models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Ticket {
    private int ticketId;
    @NonNull private String title;
    private String description;
    @NonNull private Status currentStatus;
    private short priority;
    @NonNull private Queue queue;
    private int localTicketId;
}
