package models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class Status {
    private short statusId;
    @NonNull private String name;
}