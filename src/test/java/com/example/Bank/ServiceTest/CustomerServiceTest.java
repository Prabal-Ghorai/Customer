package com.example.Bank.ServiceTest;

import com.example.Bank.Entity.Customer;
import com.example.Bank.Entity.CustomerDocument;
import com.example.Bank.Entity.DocumentType;
import com.example.Bank.Payload.CustomerDto;
import com.example.Bank.Payload.DocumentDto;
import com.example.Bank.Repository.CustomerDocumentRepository;
import com.example.Bank.Repository.CustomerRepository;
import com.example.Bank.Service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerDocumentRepository customerDocumentRepository;

    private Customer customer;
    private CustomerDocument document;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setAccountId(1L);

        document = new CustomerDocument();
        document.setDocumentNumber("123");
        document.setCustomer(customer);
        // Remove manual reset, let MockitoExtension handle mocks
    }
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

    //  SUCCESS CASE
    @Test
    void testFindByDocumentNumber_Success() {

        Mockito.when(customerDocumentRepository.findByDocumentNumber(123))
                .thenReturn(Optional.of(document));

        Customer result = customerService.findByDocumentNumber(123);

        assertNotNull(result);
        assertEquals(1L, result.getAccountId());
    }

    //  DOCUMENT NOT FOUND
    @Test
    void testFindByDocumentNumber_NotFound() {

        Mockito.when(customerDocumentRepository.findByDocumentNumber(123))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> customerService.findByDocumentNumber(123)
        );

        assertEquals("Document not found", exception.getMessage());
    }

    // CUSTOMER FOUND
    @Test
    void testGetCustomerById_Success() {
        // Arrange
        Customer mockCustomer = new Customer();
        mockCustomer.setAccountId(1L);
        mockCustomer.setCustomerName("TestUser");
        mockCustomer.setPhNo(1234567890L);
        mockCustomer.setDocuments(new ArrayList<>());
        mockCustomer.setCreatedAt(LocalDateTime.now());

        Mockito.when(customerRepository.findById(1L))
                .thenReturn(Optional.of(mockCustomer));

        // Act
        Customer result = customerService.getCustomerById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getAccountId());
        assertEquals("TestUser", result.getCustomerName());
        assertEquals(1234567890L, result.getPhNo());
        assertTrue(result.getDocuments().isEmpty());
    }

    // CUSTOMER NOT FOUND
    @Test
    void testGetCustomerById_NotFound() {
        Mockito.when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> customerService.getCustomerById(1L)
        );

        // Accept either exact or partial match for error message
        String msg = exception.getMessage();
        assertTrue(msg.equals("Customer not found") || msg.contains("Customer not found"));
    }

}
