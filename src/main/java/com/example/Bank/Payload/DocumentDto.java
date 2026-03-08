package com.example.Bank.Payload;

import com.example.Bank.Entity.DocumentType;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {
    private DocumentType documentType;
    private String documentNumber;
}
