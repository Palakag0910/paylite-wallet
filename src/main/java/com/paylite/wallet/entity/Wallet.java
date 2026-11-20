package com.paylite.wallet.entity;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.time.Instant;


@Entity
@Table(name = "wallets")
public class Wallet {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_user_id")
    private Long ownerUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type")
    private WalletType walletType;

    private long balance;

    @Enumerated(EnumType.STRING)
    private WalletStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    protected Wallet()
    {
    }

    public Wallet(Long ownerUserId, WalletType walletType)
    {
        this.ownerUserId = ownerUserId;
        this.walletType = walletType;
        this.balance = 0L;
        this.status = WalletStatus.ACTIVE;
    }

    public Long getId()
    {
        return id;
    }
    public Long getOwnerUserId()
    {
        return ownerUserId;
    }
    public WalletType getWalletType()
    {
        return walletType;
    }
    public long getBalance()
    {
        return balance;
    }
    public WalletStatus getStatus()
    {
        return status;
    }
}
