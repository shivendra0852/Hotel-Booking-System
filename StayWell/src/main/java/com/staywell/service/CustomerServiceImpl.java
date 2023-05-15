package com.staywell.service;

import com.staywell.dto.CustomerDTO;
import com.staywell.exception.CustomerException;
import com.staywell.model.Customer;
import com.staywell.model.Reservation;
import com.staywell.repository.CustomerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class CustomerServiceImpl implements CustomerService{


    @Autowired
    private CustomerDao cDao;


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public Customer createCustomer(Customer customer) throws CustomerException {

            customer.setPassword(passwordEncoder.encode(customer.getPassword()));

            Optional<Customer> customerExist =  cDao.findByEmail(customer.getEmail());

            if(customerExist.isEmpty()){

                    customer.setRegistrationDateTime(LocalDateTime.now());

                    customer.setReservations( new ArrayList<>());

                    return cDao.save(customer);
            }

            else throw new CustomerException("Customer is already exist ...!");

    }

    @Override
    public Customer updateCustomer(CustomerDTO customerDto) throws CustomerException {

            Authentication auth =  SecurityContextHolder.getContext().getAuthentication();

            Optional<Customer> customerExist =  cDao.findByEmail(auth.getName());

            Customer customer = customerExist.get();

            if(customerDto.getName() != null) customer.setName(customerDto.getName());

            if(customerDto.getGender() != null) customer.setGender(customerDto.getGender());

            if(customerDto.getDob() != null) customer.setDob(customerDto.getDob());

            if(customerDto.getAddress() != null) customer.setAddress(customerDto.getAddress());

            if(customerDto.getEmail() != null) customer.setEmail(customerDto.getEmail());

            if(customerDto.getPhone() != null ) customer.setPhone(customerDto.getPhone());

            if(customerDto.getPassword() != null) customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));


            return cDao.save(customer);



    }

    @Override
    public Customer deleteCustomer() throws CustomerException {

           Authentication auth =  SecurityContextHolder.getContext().getAuthentication();

           Optional<Customer> customerExist =  cDao.findByEmail(auth.getName());

           Customer customer = customerExist.get();

           if(customerExist.isPresent()){

               List<Reservation> reservations =  customer.getReservations();

               reservations.forEach(el ->{

                   if(!el.getStatus().equals("CLOSED")) throw new CustomerException("Can't delete. Reservation is not closed ");
               });

               cDao.delete(customer);

               return customer;


           }
           else throw new CustomerException("Customer not exist");
    }

    @Override
    public List<Customer> getAllCustomer() throws CustomerException {

        List<Customer> customers = cDao.findAll();

        if(!customers.isEmpty()){

            return customers;
        }

        else throw new CustomerException("Customers not exist");
    }

    @Override
    public Customer getCustomerById(Integer id) throws CustomerException {

        Optional<Customer> customerExist = cDao.findById(id);

        if(customerExist.isPresent()){

            Customer customer = customerExist.get();

            return customer;
        }

        else throw new CustomerException("Customer not exist by this Id : "+id);
    }
}
