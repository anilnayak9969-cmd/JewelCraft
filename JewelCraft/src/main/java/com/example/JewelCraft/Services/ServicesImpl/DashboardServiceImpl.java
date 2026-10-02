package com.example.JewelCraft.Services.ServicesImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.JewelCraft.Dto.AdminDashboardResponse;
import com.example.JewelCraft.Dto.LabelValue;
import com.example.JewelCraft.Dto.ReportResponse;
import com.example.JewelCraft.Dto.StoreDashboardResponse;
import com.example.JewelCraft.Enum.OrderStatus;
import com.example.JewelCraft.Enum.RequestStatus;
import com.example.JewelCraft.Enum.RoleName;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Mapper.OrderMapper;
import com.example.JewelCraft.Mapper.ProductMapper;
import com.example.JewelCraft.Repositories.CustomerRequestRepository;
import com.example.JewelCraft.Repositories.InventoryRepository;
import com.example.JewelCraft.Repositories.OrderItemRepository;
import com.example.JewelCraft.Repositories.OrderRepository;
import com.example.JewelCraft.Repositories.PaymentRepository;
import com.example.JewelCraft.Repositories.ProductRepository;
import com.example.JewelCraft.Repositories.UserRepository;
import com.example.JewelCraft.Services.DashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

	private static final List<OrderStatus> PENDING = List.of(OrderStatus.PLACED, OrderStatus.CONFIRMED);
	private static final List<OrderStatus> IN_STORE = List.of(OrderStatus.PROCESSING, OrderStatus.PACKED);
	private static final List<OrderStatus> NOT_SHIPPED = List.of(OrderStatus.PLACED, OrderStatus.CONFIRMED,
			OrderStatus.PROCESSING, OrderStatus.PACKED);
	private static final List<RequestStatus> OPEN_REQUESTS = List.of(RequestStatus.OPEN, RequestStatus.IN_PROGRESS);
	private static final DateTimeFormatter MONTH_LABEL = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);
	private static final LocalDateTime BEGINNING = LocalDate.of(2000, 1, 1).atStartOfDay();

	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final InventoryRepository inventoryRepository;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final PaymentRepository paymentRepository;
	private final CustomerRequestRepository requestRepository;
	private final OrderMapper orderMapper;
	private final ProductMapper productMapper;

	@Override
	@Transactional(readOnly = true)
	public AdminDashboardResponse getAdminDashboard() {
		LocalDateTime now = LocalDateTime.now();
		YearMonth thisMonth = YearMonth.now();

		List<LabelValue> usersByRole = new ArrayList<>();
		Map<Object, Long> roleCounts = toCountMap(userRepository.countGroupedByRole());
		for (RoleName role : RoleName.values()) {
			usersByRole.add(new LabelValue(role.name(), roleCounts.getOrDefault(role, 0L), null));
		}

		return AdminDashboardResponse.builder()
				.totalUsers(userRepository.count())
				.totalCustomers(userRepository.countByRole_Name(RoleName.CUSTOMER))
				.totalProducts(productRepository.countByActiveTrue())
				.totalOrders(orderRepository.count())
				.totalRevenue(paymentRepository.sumReceivedAllTime())
				.pendingOrders(orderRepository.countByStatusIn(NOT_SHIPPED))
				.lowStockProducts(inventoryRepository.countLowStock())
				.deliveredOrders(orderRepository.countByStatus(OrderStatus.DELIVERED))
				.cancelledOrders(orderRepository.countByStatus(OrderStatus.CANCELLED))
				.openRequests(requestRepository.countByStatusIn(OPEN_REQUESTS))
				.ordersByStatus(ordersByStatus(BEGINNING, now))
				.monthlySales(monthlySales(thisMonth.minusMonths(5), thisMonth))
				.productsByCategory(productRepository.countActiveGroupedByCategoryName().stream()
						.map(r -> new LabelValue((String) r[0], ((Number) r[1]).longValue(), null)).toList())
				.usersByRole(usersByRole)
				.recentOrders(orderRepository.findTop5ByOrderByCreatedAtDesc().stream().map(orderMapper::toResponse).toList())
				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public StoreDashboardResponse getStoreDashboard(boolean includeSales) {
		LocalDateTime now = LocalDateTime.now();
		return StoreDashboardResponse.builder()
				.totalProducts(productRepository.countByActiveTrue())
				.availableStock(inventoryRepository.totalAvailableStock())
				.lowStockCount(inventoryRepository.countLowStock())
				.todaysOrders(orderRepository.countByCreatedAtGreaterThanEqual(LocalDate.now().atStartOfDay()))
				.pendingOrders(orderRepository.countByStatusIn(PENDING))
				.processingOrders(orderRepository.countByStatusIn(IN_STORE))
				.openRequests(requestRepository.countByStatusIn(OPEN_REQUESTS))
				.totalStoreSales(includeSales ? orderRepository.sumSales(BEGINNING, now) : null)
				.lowStockItems(inventoryRepository.findLowStock().stream().limit(6).map(productMapper::toResponse).toList())
				.recentOrders(orderRepository.findTop5ByOrderByCreatedAtDesc().stream().map(orderMapper::toResponse).toList())
				.ordersByStatus(ordersByStatus(BEGINNING, now))
				.build();
	}

	@Override
	@Transactional(readOnly = true)
	public ReportResponse getReport(LocalDate from, LocalDate to, boolean includePayments) {
		LocalDate end = to == null ? LocalDate.now() : to;
		LocalDate start = from == null ? end.minusDays(29) : from;
		if (start.isAfter(end)) {
			throw new BadRequestException("'From' date must be before 'To' date");
		}
		LocalDateTime fromDt = start.atStartOfDay();
		LocalDateTime toDt = end.plusDays(1).atStartOfDay().minusNanos(1);

		List<LabelValue> byStatus = ordersByStatus(fromDt, toDt);
		long totalOrders = orderRepository.countBetween(fromDt, toDt);
		long cancelled = byStatus.stream().filter(l -> l.label().equals(OrderStatus.CANCELLED.name()))
				.mapToLong(LabelValue::count).sum();
		BigDecimal sales = orderRepository.sumSales(fromDt, toDt);
		long billable = totalOrders - cancelled;
		BigDecimal avg = billable > 0 ? sales.divide(BigDecimal.valueOf(billable), 2, RoundingMode.HALF_UP)
				: BigDecimal.ZERO;

		ReportResponse.ReportResponseBuilder report = ReportResponse.builder()
				.from(start)
				.to(end)
				.totalOrders(totalOrders)
				.cancelledOrders(cancelled)
				.totalSales(sales)
				.averageOrderValue(avg)
				.ordersByStatus(byStatus)
				.salesByCategory(orderItemRepository.salesByCategory(fromDt, toDt).stream()
						.map(r -> new LabelValue((String) r[0], 0, (BigDecimal) r[1])).toList())
				.topProducts(orderItemRepository.topProducts(fromDt, toDt, PageRequest.of(0, 10)).stream()
						.map(r -> new LabelValue((String) r[0], ((Number) r[1]).longValue(), (BigDecimal) r[2])).toList())
				.monthlySales(monthlySales(YearMonth.from(start), YearMonth.from(end)));

		if (includePayments) {
			report.amountReceived(paymentRepository.sumReceived(fromDt, toDt))
					.paymentsByMethod(paymentRepository.receivedByMethod(fromDt, toDt).stream()
							.map(r -> new LabelValue(r[0].toString(), ((Number) r[1]).longValue(), (BigDecimal) r[2]))
							.toList());
		}
		return report.build();
	}

	/** Every status in workflow order, including those with zero orders (keeps charts stable). */
	private List<LabelValue> ordersByStatus(LocalDateTime from, LocalDateTime to) {
		Map<OrderStatus, Long> counts = new EnumMap<>(OrderStatus.class);
		for (Object[] row : orderRepository.countGroupedByStatus(from, to)) {
			counts.put((OrderStatus) row[0], ((Number) row[1]).longValue());
		}
		List<LabelValue> result = new ArrayList<>();
		for (OrderStatus s : OrderStatus.values()) {
			result.add(new LabelValue(s.name(), counts.getOrDefault(s, 0L), null));
		}
		return result;
	}

	/** One entry per month in the range, zero-filled. */
	private List<LabelValue> monthlySales(YearMonth first, YearMonth last) {
		Map<YearMonth, Object[]> rows = new HashMap<>();
		for (Object[] r : orderRepository.monthlySales(first.atDay(1).atStartOfDay(),
				last.plusMonths(1).atDay(1).atStartOfDay().minusNanos(1))) {
			rows.put(YearMonth.of(((Number) r[0]).intValue(), ((Number) r[1]).intValue()), r);
		}
		List<LabelValue> result = new ArrayList<>();
		for (YearMonth m = first; !m.isAfter(last); m = m.plusMonths(1)) {
			Object[] r = rows.get(m);
			result.add(new LabelValue(m.format(MONTH_LABEL), r == null ? 0 : ((Number) r[2]).longValue(),
					r == null ? BigDecimal.ZERO : (BigDecimal) r[3]));
		}
		return result;
	}

	private static Map<Object, Long> toCountMap(List<Object[]> rows) {
		Map<Object, Long> map = new HashMap<>();
		rows.forEach(r -> map.put(r[0], ((Number) r[1]).longValue()));
		return map;
	}
}
