package com.ziminpro.twitter.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private UUID          id;
    private UUID          author;
    private String        content;
    private LocalDateTime createdAt;
}