package com.paylite.wallet.service;

import com.paylite.wallet.dto.SignupRequest;
import com.paylite.wallet.dto.UserResponse;
import com.paylite.wallet.entity.Role;
import com.paylite.wallet.entity.User;
import com.paylite.wallet.entity.Wallet;
import com.paylite.wallet.entity.WalletType;
import com.paylite.wallet.exception.ApiException;
import com.paylite.wallet.exception.ErrorCode;
import com.paylite.wallet.repository.UserRepository;
import com.paylite.wallet.repository.WalletRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository users;
    private final WalletRepository wallets;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository users, WalletRepository wallets, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.wallets = wallets;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse signup(SignupRequest request) {
        String email = request.email().trim().toLowerCase();

        if (users.existsByEmail(email)) {
            throw new ApiException(ErrorCode.CONFLICT, "This email is already registered.");
        }
        if (users.existsByPhone(request.phone())) {
            throw new ApiException(ErrorCode.CONFLICT, "This phone number is already registered.");
        }

        User user = new User(
                request.name().trim(),
                email,
                request.phone(),
                passwordEncoder.encode(request.password()),
                Role.CUSTOMER);
        users.save(user);

        wallets.save(new Wallet(user.getId(), WalletType.CUSTOMER));

        return new UserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getPhone(), user.getRole().name());
    }
}