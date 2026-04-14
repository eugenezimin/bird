package com.ziminpro.twitter.dao;

import com.ziminpro.twitter.dtos.Constants;
import com.ziminpro.twitter.dtos.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * JDBC-backed implementation of {@link SubscriptionRepository}.
 *
 * <p>Key differences from the original implementation:</p>
 * <ul>
 *   <li>The standalone {@code producers} and {@code subscribers} tables are
 *       gone — user identity is managed entirely by UMS.  The
 *       {@code createProducer()} / {@code createSubscriber()} helpers have
 *       been removed accordingly.</li>
 *   <li>Role validation (SUBSCRIBER / PRODUCER) is still performed upstream
 *       in {@link com.ziminpro.twitter.services.SubscriptionsService} via
 *       UMSConnector before any DAO method is called.</li>
 *   <li>{@code subscriptions.created_at} is now a DATETIME column set by the
 *       DB default ({@code CURRENT_TIMESTAMP}), so we do not need to supply
 *       it explicitly on INSERT.</li>
 * </ul>
 */
@Repository
public class JdbcSubscriptionRepository implements SubscriptionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ----------------------------------------------------------------
    // Reads
    // ----------------------------------------------------------------

    @Override
    public Subscription getSubscription(UUID subscriberId) {
        List<Subscription> rows = jdbcTemplate.query(
            Constants.GET_SUBSCRIPTION,
            (rs, rowNum) -> new Subscription(
                DaoHelper.bytesArrayToUuid(rs.getBytes("subscriber_id")),
                List.of(DaoHelper.bytesArrayToUuid(rs.getBytes("producer_id"))),
                rs.getObject("created_at", LocalDateTime.class)
            ),
            subscriberId.toString()
        );

        // Collapse the one-row-per-producer result into a single Subscription.
        Subscription merged = new Subscription();
        for (Subscription row : rows) {
            if (merged.getSubscriber() == null) {
                merged.setSubscriber(row.getSubscriber());
                merged.setCreatedAt(row.getCreatedAt());
            }
            merged.addProducer(row.getProducers().getFirst());
        }
        return merged;
    }

    // ----------------------------------------------------------------
    // Writes
    // ----------------------------------------------------------------

    @Override
    public boolean createSubscription(Subscription subscription) {
        if (subscription.getSubscriber() == null
                || subscription.getProducers() == null
                || subscription.getProducers().isEmpty()) {
            return false;
        }

        try {
            for (UUID producerId : subscription.getProducers()) {
                jdbcTemplate.update(
                    Constants.CREATE_SUBSCRIPTION,
                    subscription.getSubscriber().toString(),
                    producerId.toString()
                );
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateSubscription(Subscription subscription) {
        // Replace the entire producer list: delete all, then re-insert.
        deleteSubscription(subscription.getSubscriber());
        return createSubscription(subscription);
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
}