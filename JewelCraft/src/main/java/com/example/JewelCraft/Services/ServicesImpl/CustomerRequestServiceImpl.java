package com.example.JewelCraft.Services.ServicesImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.JewelCraft.Dto.CustomerRequestCreate;
import com.example.JewelCraft.Dto.CustomerRequestResponse;
import com.example.JewelCraft.Dto.CustomerRequestUpdate;
import com.example.JewelCraft.Dto.PageResponse;
import com.example.JewelCraft.Entites.CustomerRequest;
import com.example.JewelCraft.Entites.Order;
import com.example.JewelCraft.Enum.RequestStatus;
import com.example.JewelCraft.Exception.OrderNotFoundException;
import com.example.JewelCraft.Exception.ResourceNotFoundException;
import com.example.JewelCraft.Mapper.EngagementMapper;
import com.example.JewelCraft.Repositories.CustomerRequestRepository;
import com.example.JewelCraft.Repositories.OrderRepository;
import com.example.JewelCraft.Repositories.UserRepository;
import com.example.JewelCraft.Services.CustomerRequestService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerRequestServiceImpl implements CustomerRequestService {

	private final CustomerRequestRepository requestRepository;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final EngagementMapper mapper;

	@Override
	@Transactional
	public CustomerRequestResponse createRequest(Long userId, CustomerRequestCreate req) {
		Order order = null;
		if (req.getOrderId() != null) {
			order = orderRepository.findById(req.getOrderId())
					.filter(o -> o.getUser().getId().equals(userId))
					.orElseThrow(() -> new OrderNotFoundException(req.getOrderId()));
		}
		CustomerRequest request = CustomerRequest.builder()
				.user(userRepository.findById(userId).orElseThrow())
				.order(order)
				.type(req.getType())
				.subject(req.getSubject().trim())
				.message(req.getMessage().trim())
				.status(RequestStatus.OPEN)
				.build();
		return mapper.toResponse(requestRepository.save(request));
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomerRequestResponse> getMyRequests(Long userId) {
		return requestRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(mapper::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<CustomerRequestResponse> searchRequests(RequestStatus status, int page, int size) {
		return PageResponse.of(requestRepository.search(status, ServiceUtils.page(page, size)), mapper::toResponse);
	}

	@Override
	@Transactional
	public CustomerRequestResponse updateRequest(Long handlerId, Long requestId, CustomerRequestUpdate update) {
		CustomerRequest request = requestRepository.findById(requestId)
				.orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));
		request.setStatus(update.getStatus());
		if (update.getResponse() != null) {
			request.setResponse(ServiceUtils.trimToNull(update.getResponse()));
		}
		request.setHandledBy(userRepository.getReferenceById(handlerId));
		requestRepository.flush();
		return mapper.toResponse(requestRepository.findById(requestId).orElseThrow());
	}
}
