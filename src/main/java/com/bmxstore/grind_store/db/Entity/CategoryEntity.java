package com.bmxstore.grind_store.db.Entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category_name")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;
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

}
