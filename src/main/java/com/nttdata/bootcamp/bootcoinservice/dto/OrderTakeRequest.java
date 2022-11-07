package com.nttdata.bootcamp.bootcoinservice.dto;

import javax.validation.constraints.NotBlank;

public class OrderTakeRequest {
    @NotBlank
    private String wallet;
}
