package br.com.miguel.url_shortener.repository;

import br.com.miguel.url_shortener.Entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    boolean existsByShortCode(String shortUrl);
    Optional<Url> findByShortCode(String shortCode);
}
