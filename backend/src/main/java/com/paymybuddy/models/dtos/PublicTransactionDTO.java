package com.paymybuddy.models.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicTransactionDTO {
    private Integer id;
    private PublicUserDTO sender;
    private PublicUserDTO receiver;
    private String description;
    private Long amountInCents;

}
