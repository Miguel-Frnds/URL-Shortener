package br.com.miguel.url_shortener.service;

import br.com.miguel.url_shortener.Entity.Url;
import br.com.miguel.url_shortener.dto.UrlRequestDTO;
import br.com.miguel.url_shortener.dto.UrlStatsResponseDTO;
import br.com.miguel.url_shortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    public Url createShortUrl(UrlRequestDTO data, Long expiresInDays){
        validateUrl(data);

        String shortCode = generateCode();

        if(expiresInDays == null){
            expiresInDays = 5L;
        }

        Url newUrl = new Url();
        newUrl.setOriginalUrl(data.url());
        newUrl.setShortCode(shortCode);
        newUrl.setAccessCount(0L);
        newUrl.setCreatedAt(LocalDateTime.now());
        newUrl.setExpiresAt(LocalDateTime.now().plusDays(expiresInDays));

        return urlRepository.save(newUrl);
    }

    public List<UrlStatsResponseDTO> getUrls(){
        List<Url> urls = urlRepository.findAll();

        return urls.stream()
                .map(url -> new UrlStatsResponseDTO(
                        "https://short.local/" + url.getShortCode(),
                        url.getOriginalUrl(),
                        url.getAccessCount()
                ))
                .toList();
    }

    public String getOriginalUrl(String shortUrl){
        Url url = getByShortUrl(shortUrl);

        if(url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Url expired");
        }

        url.setAccessCount(url.getAccessCount() + 1);
        urlRepository.save(url);
        return url.getOriginalUrl();
    }

    public void deleteByShortUrl(String shortUrl){
        Url url = getByShortUrl(shortUrl);
        urlRepository.delete(url);
    }

    private String generateCode(){
        int numTries = 10;
        while(numTries > 0) {
            String shortCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

            if(!urlRepository.existsByShortCode(shortCode)){
                return shortCode;
            }

            numTries--;
        }

        throw new RuntimeException("Could not generate unique short code");
    }

    private void validateUrl(UrlRequestDTO data){
        if(!data.url().startsWith("https://") && !data.url().startsWith("http://")){
            throw new RuntimeException("Invalid Url");
        }
    }

    private Url getByShortUrl(String shortUrl){
        return urlRepository.findByShortCode(shortUrl)
                .orElseThrow(() -> new RuntimeException("Short url not found"));
    }
}
