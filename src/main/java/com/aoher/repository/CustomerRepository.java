package com.aoher.repository;

import com.aoher.model.Customer;

import java.util.List;

public interface CustomerRepository {

    int save(Customer customer);

    Customer findByCustomerId(Long id);
    Customer findByCustomerId2(Long id);
    Customer findByCustomerId3(Long id);

    List<Customer> findAll();
    List<Customer> findAll2();
    List<Customer> findAll3();
    List<Customer> findAll4();

    String findCustomerNameById(Long id);
    int count();
}
