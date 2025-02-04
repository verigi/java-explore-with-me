package ru.practicum.general.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 50, unique = true)
    @Length(min = 1, max = 50)
    private String name;
}