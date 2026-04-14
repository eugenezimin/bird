package com.ziminpro.twitter.dtos;

/**
 * Application-wide constants: URI paths, HTTP header names, and all SQL
 * statements targeting the new {@code twitter} database schema
 * (02_twitter_ddl.sql / 04_twitter_dml.sql).
 *
 * <p>Schema highlights vs. the original:</p>
 * <ul>
 *   <li>{@code messages.producer_id} → {@code messages.author_id}</li>
 *   <li>{@code messages.created} (int epoch) → {@code messages.created_at} (DATETIME)</li>
 *   <li>Max message content: 140 → 280 characters</li>
 *   <li>Standalone {@code producers} and {@code subscribers} tables removed;
 *       both roles are now managed entirely by the UMS service.</li>
 *   <li>{@code subscriptions} retains {@code subscriber_id} / {@code producer_id}
 *       as logical FK references to {@code ums.users.id}.</li>
 *   <li>{@code subscriptions.created_at} (DATETIME) added.</li>
 * </ul>
 */
public final class Constants {

    private Constants() {
        throw new IllegalStateException("Constants-only class");
    }

    // ----------------------------------------------------------------
    // HTTP response envelope keys
    // ----------------------------------------------------------------
    public static final String CODE    = "code";
    public static final String MESSAGE = "message";
    public static final String DATA    = "data";

    // ----------------------------------------------------------------
    // URI paths
    // ----------------------------------------------------------------
    public static final String URI_MESSAGE       = "/messages/message";
    public static final String URI_MESSAGES      = "/messages";
    public static final String URI_PRODUCER      = "/messages/producer";
    public static final String URI_SUBSCRIBER    = "/messages/subscriber";
    public static final String URI_SUBSCRIPTION  = "/subscriptions/subscriber";
    public static final String URI_SUBSCRIPTIONS = "/subscriptions";

    // ----------------------------------------------------------------
    // HTTP header values
    // ----------------------------------------------------------------
    public static final String APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE     = "Content-Type";
    public static final String ACCEPT           = "Accept";

    // ----------------------------------------------------------------
    // Table names
    // ----------------------------------------------------------------
    private static final String TABLE_MESSAGES      = "`messages`";
    private static final String TABLE_SUBSCRIPTIONS = "`subscriptions`";

    // ----------------------------------------------------------------
    // Message queries
    //
    // Column mapping:
    //   author_id   — was producer_id
    //   created_at  — was created (int epoch)
    // ----------------------------------------------------------------

    /** Fetch a single message by its UUID. */
    public static final String GET_MESSAGE_BY_ID =
        "SELECT id, author_id, content, created_at " +
        "FROM " + TABLE_MESSAGES +
        " WHERE id = UUID_TO_BIN(?)";

    /** Fetch all messages written by a given author (producer). */
    public static final String GET_MESSAGES_FOR_PRODUCER =
        "SELECT id, author_id, content, created_at " +
        "FROM " + TABLE_MESSAGES +
        " WHERE author_id = UUID_TO_BIN(?)" +
        " ORDER BY created_at DESC";

    /**
     * Fetch all messages visible to a subscriber — i.e. messages whose
     * author the subscriber follows.
     *
     * <p>The join path is:
     * {@code messages.author_id → subscriptions.producer_id}
     * filtered by {@code subscriptions.subscriber_id}.</p>
     */
    public static final String GET_MESSAGES_FOR_SUBSCRIBER =
        "SELECT m.id, m.author_id, m.content, m.created_at " +
        "FROM " + TABLE_MESSAGES + " m " +
        "JOIN " + TABLE_SUBSCRIPTIONS + " s ON m.author_id = s.producer_id " +
        "WHERE s.subscriber_id = UUID_TO_BIN(?)" +
        " ORDER BY m.created_at DESC";

    /** Insert a new message row. {@code id} and {@code created_at} are supplied by the app. */
    public static final String CREATE_MESSAGE =
        "INSERT INTO " + TABLE_MESSAGES +
        " (id, author_id, content, created_at)" +
        " VALUES (UUID_TO_BIN(?), UUID_TO_BIN(?), ?, ?)";

    /** Hard-delete a message by its UUID. */
    public static final String DELETE_MESSAGE =
        "DELETE FROM " + TABLE_MESSAGES +
        " WHERE id = UUID_TO_BIN(?)";

    // ----------------------------------------------------------------
    // Subscription queries
    //
    // No producers/subscribers junction tables exist any more.
    // Both subscriber_id and producer_id are logical FKs to ums.users.id,
    // enforced at the application level via UMSConnector.
    // ----------------------------------------------------------------

    /** Fetch all producer UUIDs that a subscriber follows. */
    public static final String GET_SUBSCRIPTION =
        "SELECT subscriber_id, producer_id, created_at " +
        "FROM " + TABLE_SUBSCRIPTIONS +
        " WHERE subscriber_id = UUID_TO_BIN(?)";

    /**
     * Add a single (subscriber, producer) pair.
     * {@code INSERT IGNORE} makes this idempotent — re-subscribing to an
     * already-followed producer is a no-op rather than an error.
     */
    public static final String CREATE_SUBSCRIPTION =
        "INSERT IGNORE INTO " + TABLE_SUBSCRIPTIONS +
        " (subscriber_id, producer_id)" +
        " VALUES (UUID_TO_BIN(?), UUID_TO_BIN(?))";

    /** Remove all subscriptions for a given subscriber. */
    public static final String DELETE_SUBSCRIPTION =
        "DELETE FROM " + TABLE_SUBSCRIPTIONS +
        " WHERE subscriber_id = UUID_TO_BIN(?)";
}