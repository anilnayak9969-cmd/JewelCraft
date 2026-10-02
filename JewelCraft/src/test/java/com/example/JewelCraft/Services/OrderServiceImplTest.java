package com.example.JewelCraft.Services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.JewelCraft.Dto.OrderStatusRequest;
import com.example.JewelCraft.Dto.PlaceOrderRequest;
import com.example.JewelCraft.Entites.Address;
import com.example.JewelCraft.Entites.Cart;
import com.example.JewelCraft.Entites.CartItem;
import com.example.JewelCraft.Entites.Category;
import com.example.JewelCraft.Entites.Delivery;
import com.example.JewelCraft.Entites.Inventory;
import com.example.JewelCraft.Entites.Order;
import com.example.JewelCraft.Entites.OrderItem;
import com.example.JewelCraft.Entites.Payment;
import com.example.JewelCraft.Entites.Product;
import com.example.JewelCraft.Entites.Role;
import com.example.JewelCraft.Entites.User;
import com.example.JewelCraft.Enum.ApprovalStatus;
import com.example.JewelCraft.Enum.DeliveryStatus;
import com.example.JewelCraft.Enum.MetalType;
import com.example.JewelCraft.Enum.OrderStatus;
import com.example.JewelCraft.Enum.PaymentMethod;
import com.example.JewelCraft.Enum.PaymentStatus;
import com.example.JewelCraft.Enum.RoleName;
import com.example.JewelCraft.Enum.TrackingEvent;
import com.example.JewelCraft.Exception.BadRequestException;
import com.example.JewelCraft.Exception.InsufficientStockException;
import com.example.JewelCraft.Exception.UnauthorizedException;
import com.example.JewelCraft.Mapper.OrderMapper;
import com.example.JewelCraft.Mapper.UserMapper;
import com.example.JewelCraft.Repositories.AddressRepository;
import com.example.JewelCraft.Repositories.CartRepository;
import com.example.JewelCraft.Repositories.OrderRepository;
import com.example.JewelCraft.Repositories.UserRepository;
import com.example.JewelCraft.Security.UserDetailsImpl;
import com.example.JewelCraft.Services.ServicesImpl.OrderServiceImpl;
import com.example.JewelCraft.Services.ServicesImpl.PricingServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

	@Mock
	OrderRepository orderRepository;
	@Mock
	CartRepository cartRepository;
	@Mock
	AddressRepository addressRepository;
	@Mock
	UserRepository userRepository;
	@Mock
	OfferService offerService;
	@Mock
	OrderTrackingService trackingService;
	@Spy
	PricingService pricingService = new PricingServiceImpl(new BigDecimal("99"), new BigDecimal("5000"));
	@Spy
	OrderMapper orderMapper = new OrderMapper(new UserMapper(null, null, null)); // profile repositories are not used by order mapping

	@InjectMocks
	OrderServiceImpl orderService;

	private User customer;
	private Product ring;

	@BeforeEach
	void setUp() throws Exception {
		// OrderLifecycle is package-private, so create it reflectively and inject it
		var lifecycleClass = Class.forName("com.example.JewelCraft.Services.ServicesImpl.OrderLifecycle");
		var ctor = lifecycleClass.getDeclaredConstructor();
		ctor.setAccessible(true);
		var field = OrderServiceImpl.class.getDeclaredField("lifecycle");
		field.setAccessible(true);
		field.set(orderService, ctor.newInstance());

		customer = User.builder().id(1L).firstName("Priya").email("c@x.com").role(new Role(RoleName.CUSTOMER)).build();
		ring = Product.builder().id(10L).name("Gold Ring").category(Category.builder().id(1L).name("Rings").build())
				.metalType(MetalType.GOLD).purity("22K").weight(new BigDecimal("2")).metalPrice(new BigDecimal("7000"))
				.makingCharges(new BigDecimal("1000")).stoneCharges(BigDecimal.ZERO).discount(BigDecimal.ZERO)
				.tax(new BigDecimal("3")).finalPrice(new BigDecimal("15450")).active(true).build();
		ring.setInventory(Inventory.builder().product(ring).quantity(5).build());
	}

	private Cart cartWith(int qty) {
		Cart cart = Cart.builder().id(1L).user(customer).items(new ArrayList<>()).build();
		cart.getItems().add(CartItem.builder().id(1L).cart(cart).product(ring).quantity(qty).build());
		return cart;
	}

	private PlaceOrderRequest request() {
		PlaceOrderRequest req = new PlaceOrderRequest();
		req.setAddressId(7L);
		req.setPaymentMethod(PaymentMethod.COD);
		return req;
	}

	private void givenCartAndAddress(Cart cart) {
		when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
		when(addressRepository.findByIdAndUserId(7L, 1L)).thenReturn(Optional.of(Address.builder().id(7L)
				.fullName("Priya").phone("9876500005").line1("x").city("y").state("z").pincode("560001").build()));
		when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
	}

	@Test
	void placeOrderReservesStockUsesServerPriceAndEmptiesCart() {
		Cart cart = cartWith(2);
		givenCartAndAddress(cart);

		var response = orderService.placeOrder(1L, request());

		assertThat(ring.getInventory().getQuantity()).isEqualTo(3);
		assertThat(cart.getItems()).isEmpty();
		assertThat(response.getStatus()).isEqualTo(OrderStatus.PLACED);
		// (2 g x 7000 + 1000) = 15000 each, x2 = 30000, + 3% tax = 30900, free delivery
		assertThat(response.getTotalAmount()).isEqualByComparingTo("30900.00");
		assertThat(response.getPayment().getStatus()).isEqualTo(PaymentStatus.PENDING);
		verify(orderRepository).save(any(Order.class));
	}

	@Test
	void placeOrderFailsWhenNotEnoughStock() {
		givenCartAndAddress(cartWith(6));

		assertThatThrownBy(() -> orderService.placeOrder(1L, request()))
				.isInstanceOf(InsufficientStockException.class);
		verify(orderRepository, never()).save(any());
	}

	private Order placedOrder(OrderStatus status) {
		Order order = Order.builder().id(5L).orderNumber("JC1").user(customer).status(status)
				.subtotal(BigDecimal.TEN).discount(BigDecimal.ZERO).tax(BigDecimal.ZERO).deliveryCharge(BigDecimal.ZERO)
				.totalAmount(BigDecimal.TEN).items(new ArrayList<>()).build();
		order.getItems().add(OrderItem.builder().order(order).product(ring).productName("Gold Ring")
				.unitPrice(BigDecimal.TEN).taxRate(BigDecimal.ZERO).quantity(2).lineTotal(BigDecimal.TEN).build());
		order.setPayment(Payment.builder().order(order).method(PaymentMethod.COD).status(PaymentStatus.PENDING)
				.amount(BigDecimal.TEN).build());
		when(orderRepository.findById(5L)).thenReturn(Optional.of(order));
		return order;
	}

	@Test
	void customerCancelRestoresStock() {
		Order order = placedOrder(OrderStatus.PLACED);

		orderService.cancelOrder(1L, 5L, "Changed my mind");

		assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
		assertThat(ring.getInventory().getQuantity()).isEqualTo(7);
		assertThat(order.getPayment().getStatus()).isEqualTo(PaymentStatus.FAILED);
	}

	@Test
	void customerCannotCancelShippedOrder() {
		placedOrder(OrderStatus.SHIPPED);

		assertThatThrownBy(() -> orderService.cancelOrder(1L, 5L, null)).isInstanceOf(BadRequestException.class);
	}

	@Test
	void staffCannotShipOrders() {
		placedOrder(OrderStatus.PACKED);
		User staff = User.builder().id(2L).firstName("S").email("s@x.com").password("x")
				.role(new Role(RoleName.STAFF)).enabled(true).build();
		OrderStatusRequest req = new OrderStatusRequest();
		req.setStatus(OrderStatus.SHIPPED);

		assertThatThrownBy(() -> orderService.updateStatus(new UserDetailsImpl(staff), 5L, req))
				.isInstanceOf(UnauthorizedException.class);
	}

	@Test
	void storeManagerCannotMarkDelivered() {
		placedOrder(OrderStatus.OUT_FOR_DELIVERY);
		User manager = User.builder().id(3L).firstName("M").email("m@x.com").password("x")
				.role(new Role(RoleName.STORE_MANAGER)).enabled(true).build();
		OrderStatusRequest req = new OrderStatusRequest();
		req.setStatus(OrderStatus.DELIVERED);

		assertThatThrownBy(() -> orderService.updateStatus(new UserDetailsImpl(manager), 5L, req))
				.isInstanceOf(UnauthorizedException.class);
	}

	private UserDetailsImpl manager() {
		return new UserDetailsImpl(User.builder().id(3L).firstName("M").email("m@x.com").password("x")
				.role(new Role(RoleName.STORE_MANAGER)).enabled(true).build());
	}

	@Test
	void samePartnerCannotBeAssignedTwice() {
		Order order = placedOrder(OrderStatus.PACKED);
		User partner = User.builder().id(9L).firstName("Arjun").lastName("Singh").email("d@x.com").password("x")
				.role(new Role(RoleName.DELIVERY_PARTNER)).enabled(true).approvalStatus(ApprovalStatus.APPROVED).build();
		order.setDelivery(Delivery.builder().order(order).deliveryPartner(partner).status(DeliveryStatus.ASSIGNED).build());
		when(userRepository.findById(9L)).thenReturn(Optional.of(partner));

		assertThatThrownBy(() -> orderService.assignDelivery(manager(), 5L, 9L))
				.isInstanceOf(BadRequestException.class)
				.hasMessageContaining("already assigned");
	}

	@Test
	void storeUpdateIsRecordedWithoutChangingStatus() {
		Order order = placedOrder(OrderStatus.PACKED);
		UserDetailsImpl manager = manager();

		orderService.postUpdate(manager, 5L, "Delivery may be delayed by a day");

		assertThat(order.getStatus()).isEqualTo(OrderStatus.PACKED);
		verify(trackingService).record(order, TrackingEvent.STORE_UPDATE, "Delivery may be delayed by a day", manager);
	}

	@Test
	void noStoreUpdatesAfterDelivery() {
		placedOrder(OrderStatus.DELIVERED);

		assertThatThrownBy(() -> orderService.postUpdate(manager(), 5L, "Thanks for shopping"))
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	void statusCannotSkipSteps() {
		placedOrder(OrderStatus.PLACED);
		User manager = User.builder().id(3L).firstName("M").email("m@x.com").password("x")
				.role(new Role(RoleName.STORE_MANAGER)).enabled(true).build();
		OrderStatusRequest req = new OrderStatusRequest();
		req.setStatus(OrderStatus.PACKED);

		assertThatThrownBy(() -> orderService.updateStatus(new UserDetailsImpl(manager), 5L, req))
				.isInstanceOf(BadRequestException.class);
	}

	@Test
	void transitionsFollowTheWorkflow() {
		assertThat(OrderStatus.PLACED.canMoveTo(OrderStatus.CONFIRMED)).isTrue();
		assertThat(OrderStatus.DELIVERED.canMoveTo(OrderStatus.CANCELLED)).isFalse();
		assertThat(List.of(OrderStatus.PLACED, OrderStatus.CONFIRMED))
				.allMatch(OrderStatus::isCustomerCancellable);
	}
}
