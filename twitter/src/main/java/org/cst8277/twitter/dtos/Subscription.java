package org.cst8277.twitter.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    private UUID subscriber;
    private List<UUID> producers = new ArrayList<>();

    public void addProducer(UUID producerId) {
        this.producers.add(producerId);
    }
}
