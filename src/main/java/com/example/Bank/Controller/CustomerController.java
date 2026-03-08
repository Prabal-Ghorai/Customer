package com.example.Bank.Controller;


import com.example.Bank.Entity.Customer;
import com.example.Bank.Entity.CustomerDocument;
import com.example.Bank.Entity.DocumentType;
import com.example.Bank.Payload.CustomerDto;
import com.example.Bank.Repository.CustomerDocumentRepository;
import com.example.Bank.Repository.CustomerRepository;
import com.example.Bank.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1")
public class CustomerController {
    @Autowired
    CustomerService customerService;
    @Autowired
    CustomerDocumentRepository customerDocumentRepository;
    @PostMapping("/create/customer")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerDto customerDto)
    {
        Customer savedCustomer = customerService.createCustomer(customerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }
    @GetMapping
    public ResponseEntity<Customer> getByDocumentNumber(
            @RequestParam int documentNumber) {

        Customer customerdetails =
                customerService.findByDocumentNumber(documentNumber);

        return ResponseEntity.ok(customerdetails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable long id)
    {
        Customer customer=customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
}
