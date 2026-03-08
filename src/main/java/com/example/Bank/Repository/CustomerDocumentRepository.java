package com.example.Bank.Repository;

import com.example.Bank.Entity.CustomerDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument,Long> {
    Optional<CustomerDocument> findByDocumentNumber(long documentNumber);
}
