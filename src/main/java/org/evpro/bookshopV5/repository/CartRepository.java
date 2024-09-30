package org.evpro.bookshopV5.repository;


import org.evpro.bookshopV5.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId")
    Optional<Cart> findCartByUserId(@Param("userId") Integer userId);

    @Query("SELECT c FROM Cart c WHERE c.user.email = :email")
    Optional<Cart> findCartByUserEmail(@Param("email") String email);

    @Query("SELECT c FROM Cart c JOIN c.items ci WHERE ci.id = :cartItemId")
    Optional<Cart> findCartByCartItemId(@Param("cartItemId") Integer cartItemId);
}
