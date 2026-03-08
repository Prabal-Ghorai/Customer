package com.example.Bank.Service;

import com.example.Bank.Entity.Customer;
import com.example.Bank.Entity.CustomerDocument;
import com.example.Bank.Entity.DocumentType;
import com.example.Bank.Payload.CustomerDto;
import com.example.Bank.Repository.CustomerDocumentRepository;
import com.example.Bank.Repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class CustomerService {
    private static final Logger logger =
            LoggerFactory.getLogger(CustomerService.class);
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerDocumentRepository customerDocumentRepository;

    public Customer createCustomer(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setCustomerName(customerDto.getCustomerName());
        customer.setAccountId(generateRandomAccountId());
        List<CustomerDocument> customerDocumentList = customerDto.getDocument()
                .stream().map(docDto -> {
                    CustomerDocument document = new CustomerDocument();
                    // Check enum type
                    if (docDto.getDocumentType() == DocumentType.AADHAAR) {
                        document.setDocumentNumber(docDto.getDocumentNumber());
                        document.setDocumentType(DocumentType.AADHAAR);
                    } else if (docDto.getDocumentType() == DocumentType.PAN) {
                        document.setDocumentNumber(docDto.getDocumentNumber());
                        document.setDocumentType(DocumentType.PAN);
                    } else {
                        throw new IllegalArgumentException("Invalid document type: " + docDto.getDocumentType());
                    }
                    // 🔥 SET OWNING SIDE
                    document.setCustomer(customer);
                    return document;
                }).toList();
        customer.setDocuments(customerDocumentList);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setPhNo(customerDto.getPhNo());
        return customerRepository.save(customer);
    }

    private final Random random = new Random();

    private Long generateRandomAccountId() {
        boolean exists;
        long accountId;

        do {
            accountId = 100000 + random.nextInt(900000); // 100000-999999
            exists = customerRepository.existsByAccountId(accountId);
        } while (exists);

        return accountId;
    }

    public Customer findByDocumentNumber(int documentNumber) {
        CustomerDocument document = customerDocumentRepository
                .findByDocumentNumber(documentNumber)
                .orElseThrow(() ->
                        new RuntimeException("Document not found"));

        return document.getCustomer();   // 🔥 return custom
    }

    public Customer getCustomerById(Long id) {
        try {
            return customerRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Customer not found with ID: {}", id);
                        return new RuntimeException("Customer not found");
                    });
        } catch (Exception e) {
            logger.error("Error while fetching customer with ID: {}", id, e);
            throw e;
        }
    }
}
