package com.api.employeePGrest.controller;

import com.api.employeePGrest.model.Employee;
import com.api.employeePGrest.service.PostgrestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
//import java.util.Map;


@RestController
@RequestMapping("/api/employees")
public class ProxyController {

    @Autowired
    private PostgrestService postgrestService;
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Employee>> getEmpById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        return postgrestService.getEmployeeById(id, authHeader)
        		.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<List<Employee>>> getAll(@RequestHeader("Authorization") String authHeader) {
        //String token = authHeader.replace("Bearer ", "");
    	
    	System.out.println("jwtToken in ProxyController GET /employee: "
				+ authHeader);
    	
        return postgrestService.getEmployees(authHeader)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<Employee>> addEmployee(@RequestBody Employee emp,
                                                      @RequestHeader("Authorization") String authHeader) {
        //String token = authHeader.replace("Bearer ", "");
    	
    	System.out.println("jwtToken in ProxyController: "
				+ authHeader);
    	System.out.println("emp POST /employee payload: in ProxyController: "
				+ emp);
    	
        return postgrestService.addEmployee(emp, authHeader)
                .map(ResponseEntity::ok);
    }
    
 // --- NEW METHOD FOR UPDATE (PATCH) ---

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<Employee>> updateEmployee(
            @PathVariable long id,
            @RequestBody Employee employee,
//            @RequestBody Map<String, Object> employee,
            @RequestHeader("Authorization") String authHeader) {
    	
//    	String token = authHeader.replace("Bearer ", "");
    	
    	System.out.println("PATCH /employee/" + id + " updates: in ProxyController: " + employee);
        
        return postgrestService.updateEmployee(id, employee, authHeader)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build()); // Handle case where employee is not found
    }


    // --- NEW METHOD FOR DELETE ---

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEmployee(
            @PathVariable long id,
            @RequestHeader("Authorization") String authHeader) {
    	
//    	String token = authHeader.replace("Bearer ", "");
    	
    	System.out.println("DELETE /employee/" + id);
        
        return postgrestService.deleteEmployee(id, authHeader)
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().<Void>build())); // Return 204 No Content on success
    	
//    	return postgrestService.deleteEmployee(id, authHeader)
//                .then(Mono.fromCallable(() ->
//                        ResponseEntity.ok("Employee with id " + id + " deleted successfully")));
    }

}










