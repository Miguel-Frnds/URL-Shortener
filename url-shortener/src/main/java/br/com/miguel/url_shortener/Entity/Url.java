package br.com.miguel.url_shortener.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "links")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String originalUrl;

    @Column(nullable = false, unique = true)
    private String shortCode;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long accessCount;

    @Column(updatable = false)
    private LocalDateTime expiresAt;
}
