package models;

import lombok.Builder;
import lombok.Data;

import java.sql.Time;

@Builder
@Data
public class Comment {
    private int commentId;
    private Ticket ticket;
    private Time creationTime;
    private String contents;
    private Person author;

}
