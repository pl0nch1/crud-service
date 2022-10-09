package models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.sql.Time;

@Builder
@Data
public class Comment {
    private int commentId;
    @NonNull private Ticket ticket;
    @NonNull private Time creationTime;
    private String contents;
    private Person author;

}
