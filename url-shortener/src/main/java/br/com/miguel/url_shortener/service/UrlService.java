package br.com.miguel.url_shortener.service;

import br.com.miguel.url_shortener.Entity.Url;
import br.com.miguel.url_shortener.dto.UrlRequestDTO;
import br.com.miguel.url_shortener.dto.UrlStatsResponseDTO;
import br.com.miguel.url_shortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    public Url createShortUrl(UrlRequestDTO data, Long expiresInDays){
        validateUrl(data);

        String shortCode = generateShortUrl();

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
                        "https://xxx.com/" + url.getShortCode(),
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

    private String generateShortUrl(){
        int length = (int) (Math.random() * ((10 - 5) + 1) + 5);

        String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < length; i++){
            int index = (int)(alphaNumericString.length() * Math.random());
            sb.append(alphaNumericString.charAt(index));
        }

        return sb.toString();
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
