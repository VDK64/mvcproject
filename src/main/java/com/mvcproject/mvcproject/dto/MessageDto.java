package com.mvcproject.mvcproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDto {
    @NotNull
    private String from;
    @NotNull
    private String to;
    @NotNull
    private String text;
    @NotNull
    private Date date;

}
