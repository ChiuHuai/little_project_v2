package com.example.littleProject.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnrealRequest {
    @NotBlank(message = "branchNo may not be null")
    @Length(min = 4, max = 4, message = "should be 4 characters")
    private String branchNo;
    @NotBlank(message = "custSeq may not be null")
    @Length(min = 2, max = 2, message = "should be 2 characters")
    private String custSeq;
    private String stock;
}
