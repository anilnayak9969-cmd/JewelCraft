package com.example.JewelCraft.Enum;

public enum VehicleType {
	BICYCLE, BIKE, SCOOTER, CAR, VAN;

	/** A bicycle needs no registration number or driving licence. */
	public boolean needsLicence() {
		return this != BICYCLE;
	}
}
