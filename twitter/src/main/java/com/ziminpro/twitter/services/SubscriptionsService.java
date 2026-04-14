package com.ziminpro.twitter.services;

import com.ziminpro.twitter.dao.SubscriptionRepository;
import com.ziminpro.twitter.dtos.Constants;
import com.ziminpro.twitter.dtos.HttpResponseExtractor;
import com.ziminpro.twitter.dtos.Roles;
import com.ziminpro.twitter.dtos.Subscription;
import com.ziminpro.twitter.dtos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SubscriptionsService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UMSConnector umsConnector;

    @Value("${ums.paths.user}")
    private String uriUser;

    // ----------------------------------------------------------------
    // Public API
    // ----------------------------------------------------------------

    public Mono<ResponseEntity<Map<String, Object>>> getSubscriptionsForSubscriberById(UUID subscriberId) {
        return umsConnector.retrieveUmsData(uriUser + "/" + subscriberId)
            .flatMap(res -> {
                Map<String, Object> response = new HashMap<>();
                User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);

                Subscription subscription = new Subscription();
                if (user.hasRole(Roles.SUBSCRIBER)) {
                    subscription = subscriptionRepository.getSubscription(subscriberId);
                }

                if (subscription.getSubscriber() == null) {
                    response.put(Constants.CODE,    "404");
                    response.put(Constants.MESSAGE, "No subscription found for user " + subscriberId);
                    response.put(Constants.DATA,    subscription);
                } else {
                    response.put(Constants.CODE,    "200");
                    response.put(Constants.MESSAGE, "Subscription retrieved successfully");
                    response.put(Constants.DATA,    subscription);
                }
                return Mono.just(jsonOk(response));
            });
    }

    public Mono<ResponseEntity<Map<String, Object>>> createSubscription(Subscription subscription) {
        return umsConnector.retrieveUmsData(uriUser + "/" + subscription.getSubscriber())
            .flatMap(res -> {
                Map<String, Object> response = new HashMap<>();
                User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);

                boolean created = false;
                if (user.hasRole(Roles.SUBSCRIBER)) {
                    created = subscriptionRepository.createSubscription(subscription);
                }

                if (!created) {
                    response.put(Constants.CODE,    "500");
                    response.put(Constants.MESSAGE, "Subscription could not be created — subscriber must have SUBSCRIBER role");
                    response.put(Constants.DATA,    false);
                } else {
                    response.put(Constants.CODE,    "201");
                    response.put(Constants.MESSAGE, "Subscription created successfully");
                    response.put(Constants.DATA,    true);
                }
                return Mono.just(jsonOk(response));
            });
    }

    public Mono<ResponseEntity<Map<String, Object>>> updateSubscriptionForSubscriberById(Subscription subscription) {
        return umsConnector.retrieveUmsData(uriUser + "/" + subscription.getSubscriber())
            .flatMap(res -> {
                Map<String, Object> response = new HashMap<>();
                User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);

                boolean updated = false;
                if (user.hasRole(Roles.SUBSCRIBER)) {
                    updated = subscriptionRepository.updateSubscription(subscription);
                }

                if (!updated) {
                    response.put(Constants.CODE,    "500");
                    response.put(Constants.MESSAGE, "Subscription could not be updated");
                    response.put(Constants.DATA,    false);
                } else {
                    response.put(Constants.CODE,    "200");
                    response.put(Constants.MESSAGE, "Subscription updated successfully");
                    response.put(Constants.DATA,    true);
                }
                return Mono.just(jsonOk(response));
            });
    }

    public Mono<ResponseEntity<Map<String, Object>>> deleteSubscriptionForSubscriberById(UUID subscriberId) {
        return umsConnector.retrieveUmsData(uriUser + "/" + subscriberId)
            .flatMap(res -> {
                Map<String, Object> response = new HashMap<>();
                User user = HttpResponseExtractor.extractDataFromHttpClientResponse(res, User.class);

                boolean deleted = false;
                if (user.hasRole(Roles.SUBSCRIBER)) {
                    deleted = subscriptionRepository.deleteSubscription(subscriberId);
                }

                if (!deleted) {
                    response.put(Constants.CODE,    "500");
                    response.put(Constants.MESSAGE, "Subscription could not be deleted");
                    response.put(Constants.DATA,    false);
                } else {
                    response.put(Constants.CODE,    "200");
                    response.put(Constants.MESSAGE, "Subscription deleted successfully");
                    response.put(Constants.DATA,    true);
                }
                return Mono.just(jsonOk(response));
            });
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    private static ResponseEntity<Map<String, Object>> jsonOk(Map<String, Object> body) {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, Constants.APPLICATION_JSON)
            .header(Constants.ACCEPT,         Constants.APPLICATION_JSON)
            .body(body);
    }
}