package com.market.cart.authentication;

import com.market.cart.entity.user.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * This custom service implements the systemic UserDetailsService of SpringSecurity.
 * It Needs the Security dependency in build.gradle to work.
 * The reason we need this custom class is to bring back the Principal User
 *   from the Security Context(Spring Security Repository for authenticated Users),
 *   in order to be compared with the credentials
 */

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override   /// UserDetailsService method. Mandatory implementation
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                                    /// Spring exception
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
