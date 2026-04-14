package com.ziminpro.twitter.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    private UUID          subscriber;
    private List<UUID>    producers  = new ArrayList<>();
    private LocalDateTime createdAt;

    /** Convenience constructor used by the API layer (no timestamp needed). */
    public Subscription(UUID subscriber, List<UUID> producers) {
        this.subscriber = subscriber;
        this.producers  = producers != null ? producers : new ArrayList<>();
    }

    public void addProducer(UUID producerId) {
        this.producers.add(producerId);
    }
}