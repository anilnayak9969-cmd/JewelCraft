package com.example.JewelCraft.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Used both for saved addresses and for the address snapshot stored on an order (id is null then). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
	private Long id;
	private String fullName;
	private String phone;
	private String line1;
	private String line2;
	private String city;
	private String state;
	private String pincode;
	private String country;
	private boolean defaultAddress;
}
