package com.example.littleProject.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MSTMBRequest {
    @DecimalMin(value = "0", inclusive = false, message = "qty must be greater than 0")
    @Digits(integer = 9, fraction = 0, message = "digits of qty is not correct")
    BigDecimal curPrice;
    @NotBlank(message = "stock should not be blank")
    @Length(max = 6, message = "stock should less than 6 characters")
    String stock;
}
