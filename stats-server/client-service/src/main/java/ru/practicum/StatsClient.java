package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.util.ArrayList;
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

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        StringBuilder uriBuilder = new StringBuilder(url + "/stats?start={start}&end={end}");
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                uriBuilder.append("&uris=").append(uri);
            }
        }

        if (unique != null) {
            uriBuilder.append("&unique=").append(unique);
        }

        Object responseBody = rest.getForEntity(
                uriBuilder.toString(),
                Object.class, parameters).getBody();

        List<ViewStatsDto> stats = new ArrayList<>();
        if (responseBody != null) {
            List<Map<String, Object>> body = (List<Map<String, Object>>) responseBody;
            if (body != null && body.size() > 0) {
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
    }
}
