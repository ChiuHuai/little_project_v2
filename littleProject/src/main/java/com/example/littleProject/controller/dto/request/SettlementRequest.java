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
public class SettlementRequest {
    @NotBlank(message = "branchNo should not be blank")
    @Length(min = 4, max = 4, message = "branchNo should be 4 characters")
    private String branchNo;
    @NotBlank(message = "custSeq should not be blank")
    @Length(min = 2, max = 2, message = "custSeq should be 2 characters")
    private String custSeq;
}
