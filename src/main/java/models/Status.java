package models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Status {
    private short statusId;
    private String name;
}