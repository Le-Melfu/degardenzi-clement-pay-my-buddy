package com.paymybuddy.models.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicUserDTO {
    private Integer id;
    private String username;
    private String email;
}
