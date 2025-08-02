package com.example.security.service;

import com.example.core.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    // Note: This will be injected when infra module is set up
    // private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // TODO: Implement with actual repository when infra module is ready
        // User user = userRepository.findByEmail(email)
        //         .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        // 
        // return UserPrincipal.create(user);
        
        throw new UnsupportedOperationException("UserRepository not yet implemented");
    }
    
    public static class UserPrincipal implements UserDetails {
        private final Long id;
        private final String email;
        private final String password;
        private final List<GrantedAuthority> authorities;
        private final boolean active;
        
        public UserPrincipal(Long id, String email, String password, List<GrantedAuthority> authorities, boolean active) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.authorities = authorities;
            this.active = active;
        }
        
        public static UserPrincipal create(User user) {
            List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );
            
            return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getActive()
            );
        }
        
        public Long getId() {
            return id;
        }
        
        @Override
        public String getUsername() {
            return email;
        }
        
        @Override
        public String getPassword() {
            return password;
        }
        
        @Override
        public List<GrantedAuthority> getAuthorities() {
            return authorities;
        }
        
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }
        
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }
        
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }
        
        @Override
        public boolean isEnabled() {
            return active;
        }
    }
}