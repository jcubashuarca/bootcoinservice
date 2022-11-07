package com.nttdata.bootcamp.bootcoinservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodRequest {
    @NotBlank(message = "wallet cannot be empty")
    private String wallet;
    @NotBlank(message = "accountType cannot be empty")
    @Pattern(regexp = "^(yanki|accountbank)$", message = "The documentType must have a value from: 'yanki' or 'accountbank'")
    private String accountType;
    private String accountNumber;

    public MessageKafka toMessageKafka() {
        return MessageKafka.builder()
                .type("request")
                .document("account")
                .number(this.accountNumber)
                .build();
    }
}
