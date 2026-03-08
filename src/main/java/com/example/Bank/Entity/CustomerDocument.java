package com.example.Bank.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "customer_documents")
public class CustomerDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID documentId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @Column(nullable = false, unique = true)
    private String documentNumber;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "accountId")
    private Customer customer;
}
