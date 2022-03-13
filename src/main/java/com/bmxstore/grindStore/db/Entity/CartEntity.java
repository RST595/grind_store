package com.bmxstore.grindStore.db.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@Entity
@Table(name="cart")
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private ProductEntity productEntity;

    @JsonIgnore
    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity userEntity;

    private int quantity;

    public CartEntity(ProductEntity product, int quantity, UserEntity user){
        this.userEntity = user;
        this.productEntity = product;
        this.quantity = quantity;
        this.createdDate = LocalDate.now();
    }

}
