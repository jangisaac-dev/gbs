package dev.oth.gbs.interfaces;

import dev.oth.gbs.domain.User;
import dev.oth.gbs.enums.UserRole;
import dev.oth.gbs.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 회원가입 처리
    public User.UserVo signup(User.UserSignUpDto userLoginDto) {
        User.UserEntity newUser = new User.UserEntity();
        newUser.setEmail(userLoginDto.getEmail());
        newUser.setName(userLoginDto.getName());
        newUser.setPw(passwordEncoder.encode(userLoginDto.getPassword()));  // 비밀번호 암호화
        newUser.setRole(UserRole.ROLE_USER);
        userRepository.save(newUser);
        return newUser.toVo();
    }

    // 로그인 유효성 검사
    public User.UserEntity validateUser(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPw()))
                .orElse(null);
    }

    // 전체 사용자 목록 반환
    public List<User.UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}

