package com.example.khoilnm.repository;

import com.example.khoilnm.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findAllByIsDelete(int isDelete);

    List<Product> findAllByIdInAndIsDelete(Collection<String> ids, int isDelete);

    Optional<Product> findByIdAndIsDelete(String id, int isDelete);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in :ids and p.isDelete = 0 order by p.id")
    List<Product> findAllByIdInForUpdate(Collection<String> ids);
}
