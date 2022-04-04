package com.bmxstore.grind_store.db.entity.user;

import com.bmxstore.grind_store.db.entity.order.OrderEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column
    private String address;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column
    private UserStatus status;

    @Column(nullable = false)
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "userEntity",
            fetch = FetchType.LAZY)
    private List<OrderEntity> orders;
}