package com.paylite.wallet.repository;

import com.paylite.wallet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User , Long>{
    boolean existsByEmail(String email);
    //checks if email is existing or not in any row

    boolean existsByPhone(String phone);

    Optional<User> findByEmail(String email);
}
