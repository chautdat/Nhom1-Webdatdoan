package com.nhom1ck.webdatdoan.dto.address;

import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class AddressRequest {

    @NotBlank(message = "Recipient name is required")
    @JsonAlias({"fullName", "name", "recipient", "recipient_name", "recipientName",
        "full_name", "FullName", "RecipientName"})
    private String recipientName;

    @NotBlank(message = "Phone is required")
    @JsonAlias({"phoneNumber", "phone_number", "recipientPhone", "mobile", "phoneNo", "phone_no"})
    private String phone;

    @NotBlank(message = "Address is required")
    @JsonAlias({"address", "address_line", "street", "addressLine", "addressDetail",
        "address_detail", "detailAddress", "detail_address"})
    private String addressLine;

    @JsonAlias({"wardName", "ward_name"})
    private String ward;
    @JsonAlias({"districtName", "district_name"})
    private String district;
    @JsonAlias({"cityName", "city_name", "province"})
    private String city;
    @JsonAlias({"default", "is_default", "defaultAddress", "default_address"})
    private Boolean isDefault;
    private String note;
}
