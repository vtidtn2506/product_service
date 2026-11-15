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

    /**
     * SELECT ... FOR UPDATE (pessimistic write lock) on the non-deleted products.
     * Other transactions deducting the same products wait until this one commits/rolls back.
     * ORDER BY id: every transaction locks rows in the same order → no deadlock between two orders
     * that contain the same products in a different order.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in :ids and p.isDelete = 0 order by p.id")
    List<Product> findAllByIdInForUpdate(Collection<String> ids);
}
