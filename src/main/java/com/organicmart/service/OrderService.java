package com.organicmart.service;

import com.organicmart.entity.Order;
import com.organicmart.entity.OrderStatus;
import com.organicmart.entity.User;
import java.util.List;

public interface OrderService {
    Order placeOrder(User customer, Long addressId, String paymentMethod);
    List<Order> getOrdersForCustomer(User customer);
    Order getByOrderNumber(String orderNumber);
    void updateStatus(Long orderId, OrderStatus status, String remarks);
    void assignDeliveryPartner(Long orderId, Long deliveryPartnerUserId);
}
