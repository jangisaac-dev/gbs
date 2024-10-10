package dev.oth.gbs.enums;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public enum UserRole {
    ROLE_USER,    // 일반 사용자
    ROLE_ADMIN;    // 관리자


    // 역할에 따른 권한 부여
    public List<GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.name()));
    }
}
