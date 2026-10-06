package com.organicmart.repository;
import com.organicmart.entity.FarmerProfile;
import com.organicmart.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByFarmerProfile(FarmerProfile farmerProfile);
}
