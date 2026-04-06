package com.pdq.entity;

public enum UserRole {
    ROLE_ADMIN,
    ROLE_STAFF,
    ROLE_CUSTOMER;
    
    // Optional: Method để get role name không có prefix
    public String getRoleName() {
        return this.name().substring(5); // Bỏ "ROLE_" prefix
    }
}