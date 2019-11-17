package com.mvcproject.mvcproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DialogDtoResponse {
    private Long dialogId;
    private String firstname;
    private String lastname;
    private String username;
}
