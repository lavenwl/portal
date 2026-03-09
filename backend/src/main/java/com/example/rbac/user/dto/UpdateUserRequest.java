package com.example.rbac.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UpdateUserRequest {

    @NotBlank(message = "email is required")
    @Email(message = "email format is invalid")
    private String email;

    @Size(max = 64, message = "nickname length must be <= 64")
    private String nickname;

    @Size(max = 32, message = "phone length must be <= 32")
    private String phone;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
