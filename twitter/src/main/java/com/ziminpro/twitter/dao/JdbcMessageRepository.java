package com.ziminpro.twitter.dao;

import com.ziminpro.twitter.dtos.Constants;
import com.ziminpro.twitter.dtos.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * JDBC-backed implementation of {@link MessageRepository}.
 *
 * <p>Schema changes vs. the original implementation:</p>
 * <ul>
 *   <li>{@code messages.producer_id} → {@code messages.author_id}</li>
 *   <li>{@code messages.created} (int Unix epoch) → {@code messages.created_at} (DATETIME)</li>
 *   <li>The standalone {@code producers} table no longer exists — user identity
 *       is managed entirely by the UMS service.  The {@code createProducer()}
 *       helper has been removed accordingly.</li>
 * </ul>
 */
@Repository
public class JdbcMessageRepository implements MessageRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ----------------------------------------------------------------
    // Row mapper
    // ----------------------------------------------------------------

    private static final RowMapper<Message> MESSAGE_ROW_MAPPER = (rs, rowNum) ->
        new Message(
            DaoHelper.bytesArrayToUuid(rs.getBytes("id")),
            DaoHelper.bytesArrayToUuid(rs.getBytes("author_id")),
            rs.getString("content"),
            rs.getObject("created_at", LocalDateTime.class)
        );

    // ----------------------------------------------------------------
    // Reads
    // ----------------------------------------------------------------

    @Override
    public Message getMessageById(UUID messageId) {
        List<Message> messages = jdbcTemplate.query(
            Constants.GET_MESSAGE_BY_ID,
            MESSAGE_ROW_MAPPER,
            messageId.toString()
        );
        // Return an empty sentinel instead of null so callers can check id == null.
        return messages.isEmpty()
            ? new Message()
            : messages.getFirst();
    }

    @Override
    public List<Message> getMessagesForProducerById(UUID producerId) {
        return jdbcTemplate.query(
            Constants.GET_MESSAGES_FOR_PRODUCER,
            MESSAGE_ROW_MAPPER,
            producerId.toString()
        );
    }

    @Override
    public List<Message> getMessagesForSubscriberById(UUID subscriberId) {
        return jdbcTemplate.query(
            Constants.GET_MESSAGES_FOR_SUBSCRIBER,
            MESSAGE_ROW_MAPPER,
            subscriberId.toString()
        );
    }

    // ----------------------------------------------------------------
    // Writes
    // ----------------------------------------------------------------

    @Override
    public UUID createMessage(Message message) {
        if (message.getAuthor() == null || message.getContent() == null
                || message.getContent().isBlank()) {
            return null;
        }

        UUID newId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        try {
            jdbcTemplate.update(
                Constants.CREATE_MESSAGE,
                newId.toString(),
                message.getAuthor().toString(),
                message.getContent(),
                now
            );
        } catch (Exception e) {
            // e.g. author_id does not correspond to a valid UMS user (validated
            // upstream), or a transient DB error.
            return null;
        }

        message.setId(newId);
        message.setCreatedAt(now);
        return newId;
    }

    @Override
    public int deleteMessageById(UUID messageId) {
        return jdbcTemplate.update(Constants.DELETE_MESSAGE, messageId.toString());
    }
}