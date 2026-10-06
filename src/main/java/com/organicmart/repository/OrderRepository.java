package com.organicmart.repository;
import com.organicmart.entity.Order;
import com.organicmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerOrderByPlacedAtDesc(User customer);
    List<Order> findByDeliveryPartnerOrderByPlacedAtDesc(User deliveryPartner);
    Optional<Order> findByOrderNumber(String orderNumber);
}
