package com.pdq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pdq.entity.Address;
import com.pdq.entity.User;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByUserOrderByIsDefaultDescCreatedAtDesc(User user);
    
    Optional<Address> findByUserAndIsDefaultTrue(User user);
    
    List<Address> findByUser(User user);
}