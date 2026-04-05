package com.nhom1ck.webdatdoan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nhom1ck.webdatdoan.entity.Address;
import com.nhom1ck.webdatdoan.entity.User;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByUserOrderByIsDefaultDescCreatedAtDesc(User user);
    
    Optional<Address> findByUserAndIsDefaultTrue(User user);
    
    List<Address> findByUser(User user);
}
