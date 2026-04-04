package edu.eci.dosw.DOSW_Library.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }
}