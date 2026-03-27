package br.com.miguel.url_shortener.controller;

import br.com.miguel.url_shortener.Entity.Url;
import br.com.miguel.url_shortener.dto.ShortUrlDTO;
import br.com.miguel.url_shortener.dto.UrlRequestDTO;
import br.com.miguel.url_shortener.dto.UrlResponseDTO;
import br.com.miguel.url_shortener.dto.UrlStatsResponseDTO;
import br.com.miguel.url_shortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponseDTO> createShortUrl(@RequestBody UrlRequestDTO data, @RequestParam(required = false) Long expireInDays){
        Url url = urlService.createShortUrl(data, expireInDays);
        UrlResponseDTO dto = new UrlResponseDTO(url.getOriginalUrl(), url.getShortCode());
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/links")
    public ResponseEntity<List<UrlStatsResponseDTO>> getLinks(){
        List<UrlStatsResponseDTO> urls = urlService.getUrls();
        return ResponseEntity.ok().body(urls);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode){
        String originalUrl = urlService.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build();
    }

    @DeleteMapping("/links/{shortCode}")
    public ResponseEntity<Void> deleteLink(@PathVariable("shortCode") String shortUrl){
        urlService.deleteByShortUrl(shortUrl);
        return ResponseEntity.noContent().build();
    }
}
