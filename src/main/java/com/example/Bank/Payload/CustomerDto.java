package com.example.Bank.Payload;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    private String customerName;
    private Long phNo;
    private List<DocumentDto> document;
}
