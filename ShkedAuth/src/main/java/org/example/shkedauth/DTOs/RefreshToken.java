package org.example.shkedauth.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RefreshToken {
    private String Token;
    private Date expiredAt;
}
