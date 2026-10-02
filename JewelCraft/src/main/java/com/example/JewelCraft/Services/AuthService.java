package com.example.JewelCraft.Services;

import com.example.JewelCraft.Dto.AdminRegisterRequest;
import com.example.JewelCraft.Dto.AuthResponse;
import com.example.JewelCraft.Dto.CustomerRegisterRequest;
import com.example.JewelCraft.Dto.DeliveryRegisterRequest;
import com.example.JewelCraft.Dto.LoginRequest;
import com.example.JewelCraft.Dto.ManagerRegisterRequest;
import com.example.JewelCraft.Dto.OtpSentResponse;
import com.example.JewelCraft.Dto.RegistrationResponse;
import com.example.JewelCraft.Dto.StaffRegisterRequest;
import com.example.JewelCraft.Dto.VerifyEmailRequest;
import com.example.JewelCraft.Dto.VerifyEmailResponse;

/**
 * Self sign-up for every role except ADMIN (admins are created by another admin).
 * Every sign-up must verify its email with an OTP; staff-type accounts are then
 * reviewed by an admin before they can sign in.
 */
public interface AuthService {

	RegistrationResponse registerCustomer(CustomerRegisterRequest request);

	RegistrationResponse registerStaff(StaffRegisterRequest request);

	RegistrationResponse registerManager(ManagerRegisterRequest request);

	RegistrationResponse registerDeliveryPartner(DeliveryRegisterRequest request);

	RegistrationResponse registerAdmin(AdminRegisterRequest request);

	VerifyEmailResponse verifyEmail(VerifyEmailRequest request);

	OtpSentResponse resendOtp(String email);

	AuthResponse login(LoginRequest request);
}
