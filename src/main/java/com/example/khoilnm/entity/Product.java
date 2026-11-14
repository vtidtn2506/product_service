package com.example.khoilnm.entity;

import com.khoilnm.commoncore.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "product", schema = "product")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "is_delete")
    private int isDelete;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "price")
    private int price;
}
