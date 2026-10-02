package com.example.JewelCraft.Services;

import java.util.List;

import com.example.JewelCraft.Dto.CustomerRequestCreate;
import com.example.JewelCraft.Dto.CustomerRequestResponse;
import com.example.JewelCraft.Dto.CustomerRequestUpdate;
import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Enum.RequestStatus;

public interface CustomerRequestService {

	CustomerRequestResponse createRequest(Long userId, CustomerRequestCreate request);

	List<CustomerRequestResponse> getMyRequests(Long userId);

	PageResponse<CustomerRequestResponse> searchRequests(RequestStatus status, int page, int size);

	CustomerRequestResponse updateRequest(Long handlerId, Long requestId, CustomerRequestUpdate update);
}
