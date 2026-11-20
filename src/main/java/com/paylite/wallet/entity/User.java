package com.paylite.wallet.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name= "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "pin_hash")
    private String pinHash;

    @Column(name = "pin_failed_attempts")
    private int pinFailedAttempts;

    @Column(name = "pin_locked_until")
    private Instant pinLockedUntil;

    @Column(name = "created_at" , insertable = false , updatable = false)
    private Instant cratedAt;

    protected User(){
    }


    public User(String name, String email, String phone, String passwordHash, Role role)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Long getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
    public String getEmail()
    {
        return email;
    }
    public String getPhone()
    {
        return phone;
    }
    public String getPasswordHash()
    {
        return passwordHash;
    }
    public Role getRole()
    {
        return role;
    }
    public String getPinHash()
    {
        return pinHash;
    }
    public int getPinFailedAttempts()
    {
        return pinFailedAttempts;
    }
    public Instant getPinLockedUntil()
    {
        return pinLockedUntil;
    }
    public Instant getCratedAt()
    {
        return cratedAt;

    }


}
