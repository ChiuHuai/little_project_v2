package com.example.littleProject.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    @NotBlank(message = "tradeDate should not be blank")
    @Length(min = 8, max = 8, message = "tradeDate should be 8 characters")
    private String tradeDate;
    @NotBlank(message = "branchNo should not be blank")
    @Length(min = 4, max = 4, message = "branchNo should be 4 characters")
    private String branchNo;
    @NotBlank(message = "custSeq should not be blank")
    @Length(min = 1, max = 7, message = "custSeq should less than 7 characters")
    private String custSeq;
    @NotBlank(message = "docSeq should not be blank")
    @Length(max = 5, message = "docSeq should less than 5 characters")
    private String docSeq;
    @NotBlank(message = "stock should not be blank")
    @Length(max = 6, message = "stock should less than 6 characters")
    private String stock;
    @NotNull(message = "price should not be null")
    @DecimalMin(value = "0", inclusive = false, message = "price must be greater than 0")
    @Digits(integer = 10, fraction = 4, message = "digits of price is not correct")
    private BigDecimal price;
    @NotNull(message = "qty should not be null")
    @DecimalMin(value = "0", inclusive = false, message = "qty must be greater than 0")
    @Digits(integer = 9, fraction = 0, message = "digits of qty is not correct")
    private BigDecimal qty;

}
