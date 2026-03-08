package com.example.Bank.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "customers")
public class Customer {
    @Id
    @Column(unique = true, nullable = false, length = 6)
    private Long accountId;
    @Column(nullable = false)
    private String customerName;
    @Column(unique = true,length = 10)
    private Long phNo;
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CustomerDocument> documents;
    private LocalDateTime createdAt;


}
