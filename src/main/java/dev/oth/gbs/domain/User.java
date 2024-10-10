package dev.oth.gbs.domain;

import dev.oth.gbs.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public class User {

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity(name = "tb_user")
    public static class UserEntity implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        private Long id;

        @Column(nullable = false)
        private String pw;

        @Column(nullable = false)
        private String name;

        @Column(nullable = false, unique = true)
        private String email;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private UserRole role = UserRole.ROLE_USER;  // 기본값을 ROLE_USER로 설정

        // UserDto 변환 메서드
        public UserDto toDto() {
            return new UserDto(this.email, this.pw, this.name, this.role);
        }

        // 로그인 결과로 반환할 VO 변환 메서드
        public UserVo toVo() {
            return new UserVo(this.email, this.role);
        }
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class UserBaseObject {
        protected String email;
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class UserLoginDto extends UserBaseObject {

        protected String password;  // 로그인 시 사용할 비밀번호

        // 생성자: 로그인 시 email, password만 받음
        public UserLoginDto(String email, String password) {
            super(email);
            this.password = password;
        }
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class UserSignUpDto extends UserBaseObject {

        protected String password;
        protected String name;

        public UserSignUpDto(String email, String password, String name) {
            super(email);
            this.password = password;
            this.name = name;
        }
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class UserDto extends UserBaseObject {

        protected String password;
        protected String name;
        protected UserRole role;

        public UserDto(String email, String password, String name, UserRole role) {
            super(email);
            this.password = password;
            this.name = name;
            this.role = role;
        }
    }

    @Getter
    public static class UserVo extends UserBaseObject {

        private final UserRole role;

        // 로그인 후 email과 role만 반환
        public UserVo(String email, UserRole role) {
            super(email);
            this.role = role;
        }
    }

    // Optional mapper methods for conversions between different classes
    public static UserDto convertEntityToDto(UserEntity entity) {
        return entity.toDto();
    }

    public static UserVo convertEntityToLoginVo(UserEntity entity) {
        return entity.toVo();
    }
}
