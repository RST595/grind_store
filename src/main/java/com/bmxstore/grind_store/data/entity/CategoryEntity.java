package com.bmxstore.grind_store.data.entity;


import com.bmxstore.grind_store.data.entity.product.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    private String picUrl;

    @OneToMany(mappedBy = "categoryEntity",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    List<ProductEntity> products;

    @Override
    public String toString() {
        return  "\n" + "Client {" +
                "id=" + id + "  " +
                "title=" + title + "  " +
                "picUrl=" + picUrl +  '}';
    }

    public CategoryEntity(String title, String picUrl){
        this.title = title;
        this.picUrl = picUrl;
    }

}
