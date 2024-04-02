package org.example.reqEntity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {
    @Size(min = 3)
    private String username;
    @Size(min = 8)
    private String password;
    @Email
    @Size(min = 10)
    private String email;
    private String role = "USER";
    private boolean mfaEnabled = false;
}
