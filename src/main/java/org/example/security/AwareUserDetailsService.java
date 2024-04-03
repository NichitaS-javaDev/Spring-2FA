package org.example.security;

import lombok.AllArgsConstructor;
import org.example.entity.User;
import org.example.repo.IUserRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class AwareUserDetailsService implements UserDetailsService {
    private IUserRepo userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsernameOrEmail(usernameOrEmail,usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid Username or Email"));
        String userRole = "ROLE_" + user.getRole().toUpperCase();
        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(userRole));

        return new AwareUser(user, authorities);
    }
}
