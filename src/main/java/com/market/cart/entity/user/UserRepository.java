package com.market.cart.entity.user;

import com.market.cart.entity.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findById(Long id);
    Optional<User> findByUuid(String uuid);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Optional<User> findByRole(Role role);

    Optional<User> deleteByUuid(String uuid);


//    @Query("SELECT advertisements(t) FROM User t WHERE t.username = ?1")
//    Set<Advertisement> getAllAdvertisementsForUsername(String username);

}
