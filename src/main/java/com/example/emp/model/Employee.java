package com.example.emp.model;

import com.example.emp.business.validation.NotEmptyLocalDate;
import com.example.emp.swagger.DescriptionVariables;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    private static final String CONTAIN_LETTERS_MESSAGE = "Field must contain only letters";
    private static final String STRING_PATTERN = "^[a-zA-Z\\s-]*$";

    @Min(value = 1, message = DescriptionVariables.MODEL_ID_MIN)
    @Max(value = Long.MAX_VALUE, message = DescriptionVariables.MODEL_ID_MAX)
    private Long id;

    @Pattern(regexp = STRING_PATTERN, message = CONTAIN_LETTERS_MESSAGE)
    @NotBlank
    private String name;

    @NotBlank
    private String department;

    @PastOrPresent(message = DescriptionVariables.MODEL_DATE_PAST_OR_PRESENT)
    @NotEmptyLocalDate
    private LocalDate yearOfEmployment;
}
