package com.mvcproject.mvcproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseData {
    private String token;
    private String user;
    private String opponent;
    private String errorCode;
}
