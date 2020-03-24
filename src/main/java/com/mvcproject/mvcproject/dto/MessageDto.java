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
    private String date;
    private Long dialogId;

    public MessageDto(@NotNull String from, @NotNull String to, @NotNull String text, @NotNull String date) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.date = date;
    }
}
