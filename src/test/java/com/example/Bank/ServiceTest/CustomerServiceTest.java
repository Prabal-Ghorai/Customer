package com.example.Bank.ServiceTest;

import com.example.Bank.Entity.Customer;
import com.example.Bank.Entity.CustomerDocument;
import com.example.Bank.Entity.DocumentType;
import com.example.Bank.Payload.CustomerDto;
import com.example.Bank.Payload.DocumentDto;
import com.example.Bank.Repository.CustomerRepository;
import com.example.Bank.Service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;
    @Mock
    private CustomerRepository customerRepository;

    @Test
    void testCreateCustomer_success() {
        // Arrange
        CustomerDto customerDto = new CustomerDto();
        customerDto.setCustomerName("Triguna");
        customerDto.setPhNo(9876543210L);

        DocumentDto docDto = new DocumentDto();
        docDto.setDocumentType(DocumentType.AADHAAR);
        docDto.setDocumentNumber("123456789012");

        customerDto.setDocument(List.of(docDto));

        // Mock repository save
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Customer createdCustomer = customerService.createCustomer(customerDto);

        // Assert
        assertNotNull(createdCustomer);
        assertEquals("Triguna", createdCustomer.getCustomerName());
        assertEquals(9876543210L, createdCustomer.getPhNo());
        assertNotNull(createdCustomer.getAccountId());
        assertEquals(1, createdCustomer.getDocuments().size());

        CustomerDocument document = createdCustomer.getDocuments().get(0);
        assertEquals(DocumentType.AADHAAR, document.getDocumentType());
        assertEquals("123456789012", document.getDocumentNumber());
        assertEquals(createdCustomer, document.getCustomer());

        // Verify repository call
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_invalidDocument_throwsException() {
        // Arrange
        CustomerDto customerDto = new CustomerDto();
        customerDto.setCustomerName("Triguna");
        customerDto.setPhNo(9876543210L);

        DocumentDto docDto = new DocumentDto();
        docDto.setDocumentType(null); // Invalid type
        docDto.setDocumentNumber("0000");

        customerDto.setDocument(List.of(docDto));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            customerService.createCustomer(customerDto);
        });

        assertTrue(exception.getMessage().contains("Invalid document type"));
        verify(customerRepository, never()).save(any());
    }
}
