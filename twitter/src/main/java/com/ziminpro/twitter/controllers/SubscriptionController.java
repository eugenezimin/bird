package com.ziminpro.twitter.controllers;

import com.ziminpro.twitter.dtos.Constants;
import com.ziminpro.twitter.dtos.Subscription;
import com.ziminpro.twitter.services.SubscriptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
public class SubscriptionController {

    @Autowired
    private SubscriptionsService subscriptionsService;

    @GetMapping(Constants.URI_SUBSCRIPTION + "/{subscriber-id}")
    public Mono<ResponseEntity<Map<String, Object>>> getSubscriptionBySubscriberId(
            @PathVariable("subscriber-id") UUID subscriberId) {
        return subscriptionsService.getSubscriptionsForSubscriberById(subscriberId);
    }

    @PostMapping(value = Constants.URI_SUBSCRIPTIONS, consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> createSubscription(
            @RequestBody Subscription subscription) {
        return subscriptionsService.createSubscription(subscription);
    }

    @PutMapping(value = Constants.URI_SUBSCRIPTIONS, consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> updateSubscription(
            @RequestBody Subscription subscription) {
        return subscriptionsService.updateSubscriptionForSubscriberById(subscription);
    }

    @DeleteMapping(Constants.URI_SUBSCRIPTION + "/{subscriber-id}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteSubscription(
            @PathVariable("subscriber-id") UUID subscriberId) {
        return subscriptionsService.deleteSubscriptionForSubscriberById(subscriberId);
    }
}