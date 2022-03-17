package com.bmxstore.grindStore.db.Entity;

import com.bmxstore.grindStore.dto.Enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: 17.03.2022 remove all redundant name arguments
    @JoinColumn(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "total_price")
    private Double totalPrice;

    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItems;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;
}
