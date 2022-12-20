package models;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class Queue {
    private int queueId;
    private String name;
    private int topCount;
}