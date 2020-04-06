package com.aoher.repository.impl;

import com.aoher.model.Customer;
import com.aoher.repository.CustomerRepository;
import com.aoher.util.CustomerRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private static final String QUERY_SELECT = "SELECT * FROM CUSTOMER";
    private static final String QUERY_SELECT_BY_ID = "SELECT * FROM CUSTOMER WHERE ID = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int save(Customer customer) {
        return jdbcTemplate.update(
                "insert into customer (name, age, created_date) values(?,?,?)",
                customer.getName(), customer.getAge(), LocalDateTime.now()
        );
    }

    @Override
    public Customer findByCustomerId(Long id) {
        return jdbcTemplate.queryForObject(QUERY_SELECT_BY_ID, new Object[] {id}, new CustomerRowMapper());
    }

    @Override
    public Customer findByCustomerId2(Long id) {
        return jdbcTemplate.queryForObject(QUERY_SELECT_BY_ID, new Object[] {id}, new BeanPropertyRowMapper<>(Customer.class));
    }

    @Override
    public Customer findByCustomerId3(Long id) {
        return jdbcTemplate.queryForObject(QUERY_SELECT_BY_ID, new Object[] {id}, (rs, rowNum) ->
                new Customer(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getTimestamp("created_date").toLocalDateTime()
                ));
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(QUERY_SELECT);
        rows.forEach(r -> {
            Customer obj = new Customer();
            obj.setId(((Integer) r.get("ID")).longValue());
            obj.setName((String) r.get("NAME"));
            obj.setAge(((BigDecimal) r.get("AGE")).intValue());
            obj.setCreatedDate(((Timestamp) r.get("CREATED_DATE")).toLocalDateTime());
            customers.add(obj);
        });
        return customers;
    }

    @Override
    public List<Customer> findAll2() {
        return jdbcTemplate.query(QUERY_SELECT, new CustomerRowMapper());
    }

    @Override
    public List<Customer> findAll3() {
        return jdbcTemplate.query(QUERY_SELECT, new BeanPropertyRowMapper<>(Customer.class));
    }

    @Override
    public List<Customer> findAll4() {
        return jdbcTemplate.query(QUERY_SELECT,
                (rs, rowNum) -> new Customer(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getTimestamp("created_date").toLocalDateTime()
                )
        );
    }

    @Override
    public String findCustomerNameById(Long id) {
        String sql = "SELECT NAME FROM CUSTOMER WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] {id}, String.class);
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM CUSTOMER";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
