package org.example.shkedauth.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AccessToken {
    private String Token;
    private Date expiredAt;
}
