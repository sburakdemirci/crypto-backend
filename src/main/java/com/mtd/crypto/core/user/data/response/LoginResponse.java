package com.mtd.crypto.core.user.data.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String userId;
    private String email;
    private String accessToken;
    private String refreshToken;
}
