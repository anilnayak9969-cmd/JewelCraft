package com.example.JewelCraft.Mapper;

import org.springframework.stereotype.Component;

import com.example.JewelCraft.Dto.DeliveryResponse;
import com.example.JewelCraft.Dto.OrderItemResponse;
import com.example.JewelCraft.Dto.OrderResponse;
import com.example.JewelCraft.Dto.PaymentResponse;
import com.example.JewelCraft.Entites.Delivery;
import com.example.JewelCraft.Entites.Order;
import com.example.JewelCraft.Entites.OrderItem;
import com.example.JewelCraft.Entites.Payment;
import com.example.JewelCraft.Entites.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderMapper {

	private final UserMapper userMapper;

	public OrderResponse toResponse(Order o) {
		User customer = o.getUser();
		var items = o.getItems().stream().map(this::toResponse).toList();
		return OrderResponse.builder()
				.id(o.getId())
				.orderNumber(o.getOrderNumber())
				.status(o.getStatus())
				.customerId(customer.getId())
				.customerName(customer.getFullName())
				.customerEmail(customer.getEmail())
				.customerPhone(customer.getPhone())
				.items(items)
				.totalItems(items.stream().mapToInt(OrderItemResponse::getQuantity).sum())
				.shippingAddress(userMapper.toResponse(o.getShippingAddress()))
				.subtotal(o.getSubtotal())
				.discount(o.getDiscount())
				.tax(o.getTax())
				.deliveryCharge(o.getDeliveryCharge())
				.totalAmount(o.getTotalAmount())
				.couponCode(o.getCouponCode())
				.cancelReason(o.getCancelReason())
				.cancellable(o.getStatus().isCustomerCancellable())
				.payment(o.getPayment() == null ? null : toResponse(o.getPayment()))
				.delivery(o.getDelivery() == null ? null : toResponse(o.getDelivery()))
				.createdAt(o.getCreatedAt())
				.updatedAt(o.getUpdatedAt())
				.build();
	}

	public OrderItemResponse toResponse(OrderItem i) {
		return OrderItemResponse.builder()
				.id(i.getId())
				.productId(i.getProduct() == null ? null : i.getProduct().getId())
				.productName(i.getProductName())
				.productImage(i.getProductImage())
				.metalDetails(i.getMetalDetails())
				.unitPrice(i.getUnitPrice())
				.taxRate(i.getTaxRate())
				.quantity(i.getQuantity())
				.lineTotal(i.getLineTotal())
				.build();
	}

	public PaymentResponse toResponse(Payment p) {
		Order o = p.getOrder();
		return PaymentResponse.builder()
				.id(p.getId())
				.orderId(o.getId())
				.orderNumber(o.getOrderNumber())
				.customerName(o.getUser().getFullName())
				.method(p.getMethod())
				.status(p.getStatus())
				.amount(p.getAmount())
				.gatewayOrderId(p.getGatewayOrderId())
				.transactionId(p.getTransactionId())
				.paidAt(p.getPaidAt())
				.createdAt(p.getCreatedAt())
				.build();
	}

	public DeliveryResponse toResponse(Delivery d) {
		User partner = d.getDeliveryPartner();
		return DeliveryResponse.builder()
				.id(d.getId())
				.status(d.getStatus())
				.partnerId(partner.getId())
				.partnerName(partner.getFullName())
				.partnerPhone(partner.getPhone())
				.notes(d.getNotes())
				.assignedAt(d.getAssignedAt())
				.acceptedAt(d.getAcceptedAt())
				.pickedUpAt(d.getPickedUpAt())
				.deliveredAt(d.getDeliveredAt())
				.updatedAt(d.getUpdatedAt())
				.build();
	}
}
