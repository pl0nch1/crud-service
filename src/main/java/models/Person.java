package models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Person {
    private int personId;
    private String mail;
    private String firstName;
    private String lastName;
}
