package org.evpro.bookshopV5.repository;



import org.evpro.bookshopV5.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findAllByCartId(Integer cartId);
}
