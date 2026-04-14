package com.ziminpro.twitter.dao;

import com.ziminpro.twitter.dtos.Subscription;

import java.util.UUID;

public interface SubscriptionRepository {
    Subscription getSubscription(UUID subscriberId);
    boolean      createSubscription(Subscription subscription);
    boolean      updateSubscription(Subscription subscription);
    boolean      deleteSubscription(UUID subscriberId);
}