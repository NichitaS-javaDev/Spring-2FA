package org.example.reqEntity;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @Size(min = 3)
    private String username;
    @Size(min = 8)
    private String password;
    @Size(min = 6)
    private String otp;
}
