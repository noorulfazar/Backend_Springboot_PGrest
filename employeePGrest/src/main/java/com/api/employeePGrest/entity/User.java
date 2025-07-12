// User.java (Corrected to implement the UserDetails interface)

package com.api.employeePGrest.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails { // <-- THE MOST IMPORTANT CHANGE IS HERE

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // --- Your existing getters and setters are fine ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    // ===============================================================
    //  METHODS REQUIRED BY THE UserDetails INTERFACE
    //  Spring Security will call these methods automatically.
    // ===============================================================

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // We are giving every user a simple "USER" role.
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Or add logic for this
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Or add logic for this
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Or add logic for this
    }

    @Override
    public boolean isEnabled() {
        return true; // Or add logic for this
    }
}