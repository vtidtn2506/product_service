package com.example.khoilnm.repository;

import com.example.khoilnm.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findAllByIsDelete(int isDelete);

    Optional<Product> findByIdAndIsDelete(String id, int isDelete);
}
