package com.example.JewelCraft.Dto;

import java.time.LocalDateTime;

import com.example.JewelCraft.Enum.RequestStatus;
import com.example.JewelCraft.Enum.RequestType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestResponse {
	private Long id;
	private String customerName;
	private String customerEmail;
	private Long orderId;
	private String orderNumber;
	private RequestType type;
	private String subject;
	private String message;
	private RequestStatus status;
	private String response;
	private String handledBy;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
