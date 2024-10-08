package com.marketplace.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@DiscriminatorValue("BUYER")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Buyer extends User{

    @JsonManagedReference
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites;

    //@JsonManagedReference
    @JsonIgnore
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follower> following;

    @JsonManagedReference
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @JsonManagedReference
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
    private List<CouponUsage> couponUsages;

    @OneToOne(mappedBy = "buyer")
    @JsonManagedReference
    private ShoppingCart shoppingCart;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

}
