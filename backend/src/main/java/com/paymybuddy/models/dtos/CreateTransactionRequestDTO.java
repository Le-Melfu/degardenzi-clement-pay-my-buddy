package com.paymybuddy.models.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTransactionRequestDTO {

    @NotNull
    private Integer receiverId;

    @NotNull
    @Min(value = 1, message = "Amount must be greater than 0")
    private Long amountInCents;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
}
