package dev.oth.gbs.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Board implements Serializable {
    private String accessToken;
    private String refreshToken;
}
