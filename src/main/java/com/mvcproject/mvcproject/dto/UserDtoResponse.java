package com.mvcproject.mvcproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoResponse {
    private String firstname;
    private String lastname;
    private String username;
}
