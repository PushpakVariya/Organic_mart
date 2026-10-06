package com.organicmart.serviceimpl;

import com.organicmart.entity.*;
import com.organicmart.repository.*;
import com.organicmart.service.MailService;
import com.organicmart.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final PaymentRepository paymentRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    @Override
    @Transactional
    public Order placeOrder(User customer, Long addressId, String paymentMethod) {
        Cart cart = cartRepository.findByUser(customer)
                .orElseThrow(() -> new IllegalStateException("Your cart is empty."));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Your cart is empty.");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalStateException("Delivery address not found."));

        BigDecimal total = BigDecimal.ZERO;
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .customer(customer)
                .address(address)
                .status(OrderStatus.PLACED)
                .totalAmount(BigDecimal.ZERO)
                .build();
        order = orderRepository.save(order);

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .farmerProfile(product.getFarmerProfile())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();
            order.getItems().add(orderItem);

            inventoryRepository.findByProduct(product).ifPresent(inv -> {
                int remaining = Math.max(0, inv.getQuantityAvailable() - cartItem.getQuantity());
                inv.setQuantityAvailable(remaining);
                inventoryRepository.save(inv);
            });
        }

        order.setTotalAmount(total);
        order = orderRepository.save(order);

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(paymentMethod)
                .paymentStatus("COD".equals(paymentMethod) ? "PENDING" : "SUCCESS")
                .build();
        paymentRepository.save(payment);

        historyRepository.save(OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.PLACED)
                .remarks("Order placed by customer")
                .build());

        cart.getItems().clear();
        cartRepository.save(cart);

        mailService.sendOrderConfirmationEmail(customer.getEmail(), customer.getFullName(),
                order.getOrderNumber(), total.toString());

        return order;
    }

    @Override
    public List<Order> getOrdersForCustomer(User customer) {
        return orderRepository.findByCustomerOrderByPlacedAtDesc(customer);
    }

    @Override
    public Order getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalStateException("Order not found."));
    }

    @Override
    @Transactional
    public void updateStatus(Long orderId, OrderStatus status, String remarks) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found."));
        order.setStatus(status);
        orderRepository.save(order);

        historyRepository.save(OrderStatusHistory.builder()
                .order(order)
                .status(status)
                .remarks(remarks)
                .build());

        mailService.sendOrderStatusEmail(order.getCustomer().getEmail(), order.getCustomer().getFullName(),
                order.getOrderNumber(), status.name());
    }

    @Override
    @Transactional
    public void assignDeliveryPartner(Long orderId, Long deliveryPartnerUserId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found."));
        User partner = userRepository.findById(deliveryPartnerUserId)
                .orElseThrow(() -> new IllegalStateException("Delivery partner not found."));
        order.setDeliveryPartner(partner);
        orderRepository.save(order);
    }

    private String generateOrderNumber() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "OM" + ts;
    }
}
