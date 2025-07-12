// PostgrestService.java (Using the modern, non-deprecated method)

package com.api.employeePGrest.service;

import com.api.employeePGrest.model.Employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.List;
//import java.util.Map;

@Service
public class PostgrestService {

    private final WebClient webClient;
    private final String postgrestUrl;
    
    @Autowired
    private ObjectMapper objectMapper;

    public PostgrestService(WebClient webClient, @Value("${postgrest.api.url}") String postgrestUrl) {
        this.webClient = webClient;
        this.postgrestUrl = postgrestUrl;
    }
    
 // GET /employee?id=eq.{id}
    public Mono<Employee> getEmployeeById(long id, String authHeader) {
    	
        // Use fromUriString instead of fromHttpUrl
        URI uri = UriComponentsBuilder.fromUriString(postgrestUrl)
                .path("/employee")
                .queryParam("id", "eq." + id)
                .build().toUri();
        
        return webClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .header("Prefer", "return=representation")
                .header("Accept", "application/vnd.pgrst.object+json")
                .retrieve()
                .onStatus(status -> status.isError(),
                          response -> response.bodyToMono(String.class)
                                              .flatMap(error -> Mono.error(new RuntimeException("Error: " + error))))
                .bodyToMono(Employee.class);
    }

    // GET /employee
    public Mono<List<Employee>> getEmployees(String authHeader) {
        // Use fromUriString instead of fromHttpUrl
        URI uri = UriComponentsBuilder.fromUriString(postgrestUrl).path("/employee").build().toUri();
        
        return webClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToFlux(Employee.class)
                .collectList();
    }

    // POST /employee
    public Mono<Employee> addEmployee(Employee emp, String authHeader) {
    	
    	ObjectNode employee = objectMapper.valueToTree(emp);

        // Remove the "id" field so PostgREST can auto-generate it
    	employee.remove("id");
        
        // Use fromUriString instead of fromHttpUrl
        URI uri = UriComponentsBuilder.fromUriString(postgrestUrl).path("/employee").build().toUri();

//        return webClient.post()
//                .uri(uri)
//                .header(HttpHeaders.AUTHORIZATION, authHeader)
//        .header("Prefer", "return=representation,resolution=merge-duplicates")
//                .bodyValue(employee)
//                .retrieve()
//                .bodyToMono(Employee.class);
        
        return webClient.post()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .header("Prefer", "return=representation")
                .header("Accept", "application/vnd.pgrst.object+json")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(employee)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                          response -> response.bodyToMono(String.class)
                                              .flatMap(error -> Mono.error(new RuntimeException("Error: " + error))))
                .bodyToMono(Employee.class)
                .doOnNext(e -> System.out.println("Created: " + e));
    }
    
//  return webClient.patch()
//  .uri(uri)
//  .header(HttpHeaders.AUTHORIZATION, authHeader)
//  .bodyValue(employee)
//  .retrieve()
//  .bodyToMono(Employee.class);

    // PATCH /employee?id=eq.{id}
    public Mono<Employee> updateEmployee(long id, Employee employee, String authHeader) {
    	
    	employee.setId(id); // <-- ADD THIS LINE
    	
        // Use fromUriString instead of fromHttpUrl
        URI uri = UriComponentsBuilder.fromUriString(postgrestUrl)
                .path("/employee")
                .queryParam("id", "eq." + id)
                .build().toUri();

        return webClient.patch()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .header("Prefer", "return=representation")
                .header("Accept", "application/vnd.pgrst.object+json")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(employee)
                .retrieve()
                .onStatus(status -> status.isError(),
                          response -> response.bodyToMono(String.class)
                                              .flatMap(error -> Mono.error(new RuntimeException("Error: " + error))))
                .bodyToMono(Employee.class);
    }

//  return webClient.delete()
//  .uri(uri)
//  .header(HttpHeaders.AUTHORIZATION, authHeader)
//  .retrieve()
//  .bodyToMono(Void.class);
    // DELETE /employee?id=eq.{id}
    public Mono<String> deleteEmployee(long id, String authHeader) {
        // Use fromUriString instead of fromHttpUrl
        URI uri = UriComponentsBuilder.fromUriString(postgrestUrl)
                .path("/employee")
                .queryParam("id", "eq." + id)
                .build().toUri();

        return webClient.delete()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .onStatus(status -> status.isError(),
                          response -> response.bodyToMono(String.class)
                                              .flatMap(error -> Mono.error(new RuntimeException("Error: " + error))))
                .bodyToMono(String.class);
        
    }
}







