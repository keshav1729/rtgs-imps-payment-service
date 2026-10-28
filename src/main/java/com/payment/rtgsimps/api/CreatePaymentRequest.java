package com.payment.rtgsimps.api;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreatePaymentRequest(
    @NotBlank
    @Size(max = 128)
    String beneficiaryId,

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 17, fraction = 2)
    BigDecimal amount,

    @NotBlank
    @Pattern(regexp = "INR")
    String currency,

    @NotBlank
    @Size(max = 100)
    String reference
) {}
