package br.com.miguel.url_shortener.dto;

import jakarta.validation.constraints.NotBlank;

public record UrlRequestDTO(@NotBlank String url) {
}
