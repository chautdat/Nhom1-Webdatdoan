package com.pdq.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pdq.dto.address.AddressRequest;
import com.pdq.dto.address.AddressResponse;
import com.pdq.dto.common.ApiResponse;
import com.pdq.exception.BadRequestException;
import com.pdq.service.AddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping({"/api/addresses", "/api/users/addresses"})
@PreAuthorize("isAuthenticated()")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses(
            @AuthenticationPrincipal UserDetails principal) {
        String username = requirePrincipal(principal);
        List<AddressResponse> addresses = addressService.getMyAddresses(username);
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved successfully", addresses));
    }

    @GetMapping("/default")
    public ResponseEntity<ApiResponse<AddressResponse>> getDefaultAddress(
            @AuthenticationPrincipal UserDetails principal) {
        String username = requirePrincipal(principal);
        AddressResponse address = addressService.getDefaultAddress(username);
        return ResponseEntity.ok(ApiResponse.success("Default address retrieved successfully", address));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody AddressRequest request) {
        String username = requirePrincipal(principal);
        AddressResponse created = addressService.createAddress(username, request);
        return ResponseEntity.ok(ApiResponse.success("Address created successfully", created));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {
        String username = requirePrincipal(principal);
        AddressResponse updated = addressService.updateAddress(username, addressId, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", updated));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long addressId) {
        String username = requirePrincipal(principal);
        addressService.deleteAddress(username, addressId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }

    @PutMapping("/{addressId}/set-default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long addressId) {
        String username = requirePrincipal(principal);
        AddressResponse updated = addressService.setDefaultAddress(username, addressId);
        return ResponseEntity.ok(ApiResponse.success("Default address updated successfully", updated));
    }

    private String requirePrincipal(UserDetails principal) {
        if (principal == null) {
            throw new BadRequestException("Unauthenticated");
        }
        return principal.getUsername();
    }
}
