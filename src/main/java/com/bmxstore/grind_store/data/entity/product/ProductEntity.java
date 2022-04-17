package com.bmxstore.grind_store.data.entity.product;

import com.bmxstore.grind_store.data.entity.CategoryEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String productCode;

    private String imageURL;
    private double price;
    private double weight;
    private String description;
    private ProductColor productColor;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity categoryEntity;

    @Override
    public String toString() {
        return  "\n" + "Client {" +
                "id=" + id + "  " +
                "name=" + name + "  " +
                "productCode=" + productCode +
                "imageURL=" + imageURL + '}';
    }
}
