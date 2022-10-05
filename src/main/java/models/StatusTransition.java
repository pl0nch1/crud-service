package models;

import java.sql.Time;

public class StatusTransition {
    private long transitionId;
    private Ticket ticket;
    private Status statusFrom;
    private Status statusTo;
    private Time transitionTime;
}
