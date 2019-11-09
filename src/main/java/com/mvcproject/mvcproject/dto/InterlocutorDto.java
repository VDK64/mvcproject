package com.mvcproject.mvcproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InterlocutorDto {
    private Long id;
    private String avatar;
    private String username;
}
