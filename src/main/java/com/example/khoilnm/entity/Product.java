package com.example.khoilnm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "product", schema = "product")
@Getter @Setter
@SuperBuilder
@NoArgsConstructor
public class Product {
    @Id
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "is_delete")
    private int isDelete;

    @Column(name = "category_id")
    private String categoryId;
}
