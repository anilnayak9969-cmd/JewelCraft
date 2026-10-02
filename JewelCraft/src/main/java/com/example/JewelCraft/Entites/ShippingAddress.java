package com.example.JewelCraft.Entites;

import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Copy of the customer's address taken when the order is placed, so later
 * edits or deletions of the address don't change past orders.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {
	private String shipFullName;
	private String shipPhone;
	private String shipLine1;
	private String shipLine2;
	private String shipCity;
	private String shipState;
	private String shipPincode;
	private String shipCountry;

	public static ShippingAddress from(Address a) {
		return new ShippingAddress(a.getFullName(), a.getPhone(), a.getLine1(), a.getLine2(), a.getCity(),
				a.getState(), a.getPincode(), a.getCountry());
	}
}
