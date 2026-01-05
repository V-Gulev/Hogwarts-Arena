package main.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Spell {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @ManyToOne(optional = false)
    private Wizard wizard;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpellCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpellAlignment alignment;

    @Column(nullable = false)
    private String image;

    private int power;

    @Column(nullable = false)
    private LocalDateTime createdOn;

}
