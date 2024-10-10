package dev.oth.gbs.interfaces;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.oth.gbs.domain.User;
import dev.oth.gbs.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 사용자 정보를 이메일로 찾기
        User.UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Spring Security에서 요구하는 UserDetails 형태로 반환
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // 사용자 이메일
                user.getPw(), // 암호화된 비밀번호
                true, true, true, true, // 계정 활성화 여부
                user.getRole().getAuthorities() // 사용자의 역할에 따른 권한 부여
        );
    }

    // 비밀번호 확인 및 유효성 검사를 위한 메서드
    public User.UserEntity validateUser(String email, String password) {
        // 사용자 정보를 이메일로 찾고, 비밀번호가 맞는지 확인
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPw()))  // 비밀번호가 맞는지 확인
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));
    }
}
