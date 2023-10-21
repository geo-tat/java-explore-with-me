package ru.yandex.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.yandex.practicum.dto.EndpointHitDto;
import ru.yandex.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class StatsClient {

    private final String url;
    private final RestTemplate rest;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String url, RestTemplateBuilder builder) {
        this.url = url;
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(url))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public void saveStats(EndpointHitDto endpointHitDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EndpointHitDto> request = new HttpEntity<>(endpointHitDto, headers);
        rest.exchange(url + "/hit", HttpMethod.POST, request, EndpointHitDto.class);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startDate = start.format(formatter);
        String endDate = end.format(formatter);

        StringBuilder uriBuilder = new StringBuilder(url + "/stats?start={start}&end={end}");
        Map<String, Object> parameters = Map.of(
                "start", startDate,
                "end", endDate);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                uriBuilder.append("&uris=").append(uri);
            }
        }

        if (unique != null) {
            uriBuilder.append("&unique=").append(unique);
        }
        ResponseEntity<List<ViewStatsDto>> responseEntity = rest.exchange(
                uriBuilder.toString(),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<ViewStatsDto>>() {
                },
                parameters);
        return responseEntity.getBody();
    }
}
