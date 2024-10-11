package dev.oth.gbs.domain;

import dev.oth.gbs.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDetailModel implements Serializable {
    private Long id;
    private String email;
    private UserRole role;

    public TokenDetailModel(User.UserEntity userEntity) {
        this.id = userEntity.getId();
        this.email = userEntity.getEmail();
        this.role = userEntity.getRole();
    }

    @Override
    public String toString() {
        return "TokenDetailModel{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
