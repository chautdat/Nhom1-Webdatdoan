package com.pdq.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pdq.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

   @Override
public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    // identifier có thể là username hoặc email

    // Thử tìm theo username trước
    return userRepository.findByUsername(identifier)
            .map(user -> (UserDetails) user)
            .orElseGet(() ->
                    // Nếu không có username, thử tiếp email
                    userRepository.findByEmail(identifier)
                            .map(user -> (UserDetails) user)
                            .orElseThrow(() -> new UsernameNotFoundException(
                                    "User not found with username or email: " + identifier
                            ))
            );
}

}