package br.com.miguel.url_shortener.dto;

public record UrlStatsResponseDTO(String shortUrl, String originalUrl, Long accessCount) {
}
