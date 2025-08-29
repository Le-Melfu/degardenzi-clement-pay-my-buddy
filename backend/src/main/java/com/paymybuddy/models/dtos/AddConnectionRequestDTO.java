package com.paymybuddy.models.dtos;

import com.paymybuddy.models.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class AddConnectionRequestDTO {
    @NotNull
    private User user;

    @NotNull
    @Email
    @NotEmpty
    private String connectionEmail;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getConnectionEmail() {
        return connectionEmail;
    }

    public void setConnectionEmail(String connectionEmail) {
        this.connectionEmail = connectionEmail;
    }
}
