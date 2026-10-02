package com.example.JewelCraft.Dto;

import com.example.JewelCraft.Enum.ApprovalStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Admin decision on a staff / manager / delivery application. */
@Data
public class ApprovalRequest {

	@NotNull(message = "Decision is required")
	private ApprovalStatus status;

	/** Optional message emailed to the applicant (e.g. reason for rejection). */
	@Size(max = 500, message = "Note must be at most 500 characters")
	private String note;
}
