package com.example.movies_recommendation_API.accounts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreateDTO {

    @NotEmpty(message = "Tên người dùng không được để trống.")
    @Size(min = 4, max = 50, message = "Tên người dùng phải có độ dài từ 4 đến 50 ký tự.")
    private String username;

    @Email(message = "Email không hợp lệ.")
    private String email;

    @NotEmpty(message = "Mật khẩu không được để trống.")
    @Size(min = 4, max = 255, message = "Mật khẩu phải có độ dài từ 4 đến 255 ký tự.")
    private String password;
}
