package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class StatsClient {

    private final String url;
    private final RestTemplate rest;

    public StatsClient(@Value("${stats-server.url}") String url, RestTemplate rest) {
        this.url = url;
        this.rest = rest;
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

/*
Object responseBody = rest.getForEntity(
                uriBuilder.toString(),
                Object.class, parameters).getBody();

        List<ViewStatsDto> stats = new ArrayList<>();
        if (responseBody != null) {
            List<Map<String, Object>> body = (List<Map<String, Object>>) responseBody;
            if (!body.isEmpty()) {
                for (Map<String, Object> s : body) {
                    ViewStatsDto viewStats = ViewStatsDto.builder()
                            .app(s.get("app").toString())
                            .uri(s.get("uri").toString())
                            .hits(((Number) s.get("hits")).longValue())
                            .build();
                    stats.add(viewStats);
                }
            }
        }
        return stats;
 */