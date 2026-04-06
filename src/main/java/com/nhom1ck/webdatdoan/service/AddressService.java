package com.pdq.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pdq.dto.address.AddressRequest;
import com.pdq.dto.address.AddressResponse;
import com.pdq.entity.Address;
import com.pdq.entity.User;
import com.pdq.exception.BadRequestException;
import com.pdq.exception.ResourceNotFoundException;
import com.pdq.repository.AddressRepository;
import com.pdq.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<AddressResponse> getMyAddresses(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<Address> addresses = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
        return addresses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AddressResponse getDefaultAddress(String userEmail) {
        User user = getUserByEmail(userEmail);
        Address address = addressRepository.findByUserAndIsDefaultTrue(user)
                .orElseThrow(() -> new ResourceNotFoundException("No default address found"));
        return mapToResponse(address);
    }

    @Transactional
    public AddressResponse createAddress(String userEmail, AddressRequest request) {
        User user = getUserByEmail(userEmail);

        // If this is set as default, remove default from others
        if (request.getIsDefault() != null && request.getIsDefault()) {
            removeDefaultFromAll(user);
        }

        Address address = new Address();
        address.setUser(user);
        address.setRecipientName(request.getRecipientName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setCity(request.getCity());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
        address.setNote(request.getNote());

        address = addressRepository.save(address);
        return mapToResponse(address);
    }

    @Transactional
    public AddressResponse updateAddress(String userEmail, Long addressId, AddressRequest request) {
        User user = getUserByEmail(userEmail);
        
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        // Check ownership
        if (!address.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Access denied");
        }

        // If this is set as default, remove default from others
        if (request.getIsDefault() != null && request.getIsDefault()) {
            removeDefaultFromAll(user);
        }

        address.setRecipientName(request.getRecipientName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setCity(request.getCity());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
        address.setNote(request.getNote());

        address = addressRepository.save(address);
        return mapToResponse(address);
    }

    @Transactional
    public void deleteAddress(String userEmail, Long addressId) {
        User user = getUserByEmail(userEmail);
        
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        // Check ownership
        if (!address.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Access denied");
        }

        addressRepository.delete(address);
    }

    @Transactional
    public AddressResponse setDefaultAddress(String userEmail, Long addressId) {
        User user = getUserByEmail(userEmail);
        
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        // Check ownership
        if (!address.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Access denied");
        }

        // Remove default from all
        removeDefaultFromAll(user);

        // Set this as default
        address.setIsDefault(true);
        address = addressRepository.save(address);

        return mapToResponse(address);
    }

    private void removeDefaultFromAll(User user) {
        List<Address> addresses = addressRepository.findByUser(user);
        for (Address addr : addresses) {
            if (addr.getIsDefault()) {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            }
        }
    }

    private User getUserByEmail(String emailOrUsername) {
        return userRepository.findByEmail(emailOrUsername)
                .orElseGet(() -> userRepository.findByUsername(emailOrUsername)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    private AddressResponse mapToResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setAddressId(address.getAddressId());
        response.setRecipientName(address.getRecipientName());
        response.setPhone(address.getPhone());
        response.setAddressLine(address.getAddressLine());
        response.setWard(address.getWard());
        response.setDistrict(address.getDistrict());
        response.setCity(address.getCity());
        response.setIsDefault(address.getIsDefault());
        response.setNote(address.getNote());
        response.setCreatedAt(address.getCreatedAt());
        return response;
    }
}
