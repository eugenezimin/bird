package org.cst8277.twitter.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.cst8277.twitter.dtos.Constants;
import org.cst8277.twitter.dtos.Subscription;
import org.cst8277.twitter.services.SubscriptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class SubscriptionController {

    @Autowired
    private SubscriptionsService subscriptionsService;

    Map<String, Object> response = new HashMap<>();

    @RequestMapping(method = RequestMethod.GET, path = Constants.URI_SUBSCRIPTION + "/{subscriber-id}")
    public Mono<ResponseEntity<Map<String, Object>>> getSubscriptionBySubscriberId(
            @PathVariable(value = "subscriber-id", required = true) UUID subscriberId) {
        return subscriptionsService.getSubscriptionsForSubscriberById(subscriberId);
    }

    @RequestMapping(method = RequestMethod.PUT, path = Constants.URI_SUBSCRIPTIONS, consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> up(@RequestBody Subscription subscription) {
        return subscriptionsService.updateSubscriptionForSubscriberById(subscription);
    }

    @RequestMapping(method = RequestMethod.POST, path = Constants.URI_SUBSCRIPTIONS, consumes = Constants.APPLICATION_JSON)
    public Mono<ResponseEntity<Map<String, Object>>> createSubscription(@RequestBody Subscription subscription) {
        return subscriptionsService.createSubscription(subscription);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = Constants.URI_SUBSCRIPTION + "/{subscriber-id}")
    public Mono<ResponseEntity<Map<String, Object>>> createSubscription(
            @PathVariable(value = "subscriber-id", required = true) UUID subscriberId) {
        return subscriptionsService.deleteSubscriptionForSubscriberById(subscriberId);
    }
}
