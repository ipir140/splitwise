package org.setu.splitwise.models;

import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "_user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotEmpty(message = "Name cannot be empty")
    private String name;
}

