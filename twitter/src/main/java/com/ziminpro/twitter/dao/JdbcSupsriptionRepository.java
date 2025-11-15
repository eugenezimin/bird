package com.ziminpro.twitter.dao;

import java.util.List;
import java.util.UUID;

import com.ziminpro.twitter.dtos.Constants;
import com.ziminpro.twitter.dtos.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcSupsriptionRepository implements SubscriptionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcMessageRepository jdbcMessageRepository;

    @Override
    public boolean createSubscription(Subscription subscription) {
        if (subscription.getSubscriber() == null || subscription.getProducers() == null
                || subscription.getProducers().isEmpty()
                || this.createSubscriber(subscription.getSubscriber()) == null)
            return false;

        try {
            subscription.getProducers().forEach(producerId -> {
                if (jdbcMessageRepository.createProducer(producerId) != null) {
                    jdbcTemplate.update(Constants.CREATE_SUBSCRIPTION, subscription.getSubscriber().toString(),
                            producerId.toString());
                }
            });
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateSubscription(Subscription subscription) {
        this.deleteSubscription(subscription.getSubscriber());
        return this.createSubscription(subscription);
    }

    @Override
    public Subscription getSubscription(UUID subscriberId) {
        List<Subscription> subscriptions = jdbcTemplate.query(Constants.GET_SUBSCRIPTION,
                (rs, rowNum) -> new Subscription(DaoHelper.bytesArrayToUuid(rs.getBytes("subscriptions.subscriber_id")),
		                List.of(DaoHelper.bytesArrayToUuid(rs.getBytes("subscriptions.producer_id")))),
                subscriberId.toString());

        Subscription subscription = new Subscription();
        for (Subscription oSubscriptions : subscriptions) {
            if (subscription.getSubscriber() == null) {
                subscription.setSubscriber(oSubscriptions.getSubscriber());
            }
            subscription.addProducer(oSubscriptions.getProducers().getFirst());
        }
        // better to return empty message instead of null (for automatic processing)
        return subscription;
    }

    @Override
    public boolean deleteSubscription(UUID subscriberId) {
        try {
            jdbcTemplate.update(Constants.DELETE_SUBSCRIPTION, subscriberId.toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    UUID createSubscriber(UUID subscriberId) {
        try {
            jdbcTemplate.update(Constants.CREATE_SUBSCRIBER, subscriberId.toString());
        } catch (Exception e) {
            return null;
        }
        return subscriberId;
    }
}
