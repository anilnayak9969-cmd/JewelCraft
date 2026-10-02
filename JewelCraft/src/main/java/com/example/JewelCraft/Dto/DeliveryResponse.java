package com.example.JewelCraft.Dto;

import java.time.LocalDateTime;

import com.example.JewelCraft.Enum.DeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse {
	private Long id;
	private DeliveryStatus status;
	private Long partnerId;
	private String partnerName;
	private String partnerPhone;
	private String notes;
	private LocalDateTime assignedAt;
	private LocalDateTime acceptedAt;
	private LocalDateTime pickedUpAt;
	private LocalDateTime deliveredAt;
	private LocalDateTime updatedAt;
}
