
package ru.unit_techno.car_entry_control.test_utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TestUtils {

    @Autowired
    private TestRestTemplate restTemplate;

    public  <R> R invokeGetApi(Class<R> resultType, String url, HttpStatus checkStatus, HttpHeaders headers, Object... urlParams) {
        ResponseEntity<R> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, headers), resultType, urlParams);
        assertEquals(checkStatus, response.getStatusCode());
        return response.getBody();
    }

    public <R, T extends ParameterizedTypeReference<R>> R invokeGetApi(T resultType, String url, HttpStatus checkStatus, Object... urlParams) {
        ResponseEntity<R> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, null), resultType, urlParams);
        assertEquals(checkStatus, response.getStatusCode());
        return response.getBody();
    }

    public <R, T extends ParameterizedTypeReference<R>> R invokeGetApi(T resultType, String url, HttpStatus checkStatus, HttpHeaders headers, Object... urlParams) {
        ResponseEntity<R> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, headers), resultType, urlParams);
        assertEquals(checkStatus, response.getStatusCode());
        return response.getBody();
    }

    public <R, D> R invokePutApi(Class<R> resultType, String url, HttpStatus checkStatus, D dto, Object... urlParams) {
        ResponseEntity<R> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(dto, null), resultType, urlParams);
        assertEquals(checkStatus, response.getStatusCode());
        return response.getBody();
    }

    public <R, D> R invokePostApi(Class<R> resultType, String url, HttpStatus checkStatus, D dto, Object... urlParams) {
        ResponseEntity<R> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(dto, null), resultType, urlParams);
        assertEquals(checkStatus, response.getStatusCode());
        return response.getBody();
    }

    public <R, D> R invokeDeleteApi(Class<R> resultType, String url, HttpStatus checkStatus, D dto, Object... urlParams) {
        ResponseEntity<R> response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(dto, null), resultType, urlParams);
        assertEquals(checkStatus, response.getStatusCode());
        return response.getBody();
    }

    public <R, D> R invokeDeleteApi(ParameterizedTypeReference<R> responseType, String url, HttpStatus checkStatus, D dto, Object... urlParams) {
        ResponseEntity<R> response = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(dto, null), responseType, urlParams);
        assertEquals(checkStatus, response.getStatusCode());
        return response.getBody();
    }

    public <R, D> R invokePostApi(ParameterizedTypeReference<R> responseType, String url, HttpStatus checkStatus, D dto, Object... urlParams) {
        ResponseEntity<R> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(dto, null), responseType, urlParams);
        assertEquals(checkStatus, response.getStatusCode());
        return response.getBody();
    }

    public <R, T extends ParameterizedTypeReference<R>, D> R invokePutApi(T resultType, String url, HttpStatus checkStatus, D dto, Object... urlParams) {
        ResponseEntity<R> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(dto, null), resultType, urlParams);
        assertEquals(checkStatus, response.getStatusCode());
        return response.getBody();
    }
}