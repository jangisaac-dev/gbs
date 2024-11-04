package dev.oth.gbs.enums;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public enum UserRole {
    ROLE_PUBLIC,    // 모든 사용자
    ROLE_ANY,    // 모든 사용자
    ROLE_USER,    // 일반 사용자
    ROLE_MANAGER,    // 관리자
    ROLE_ADMIN;    // 슈퍼 관리자


    // 역할에 따른 권한 부여
    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.name()));
    }
}
