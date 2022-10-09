package models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.sql.Time;

@Builder
@Data
public class StatusTransition {
    private long transitionId;
    @NonNull private Ticket ticket;
    private Status statusFrom;
    private Status statusTo;
    private Time transitionTime;
}
