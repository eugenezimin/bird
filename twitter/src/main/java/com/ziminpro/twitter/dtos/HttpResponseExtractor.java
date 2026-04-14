package com.ziminpro.twitter.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Map;

/**
 * Extracts the {@code "data"} payload from the standard UMS response envelope:
 * <pre>
 * {
 *   "code":    "200",
 *   "message": "...",
 *   "data":    { ... }
 * }
 * </pre>
 *
 * <p>A shared, pre-configured {@link ObjectMapper} is used to avoid the
 * overhead of constructing a new instance on every call.  The mapper is
 * configured with {@link JavaTimeModule} so that {@code LocalDateTime}
 * fields (e.g. {@code User.createdAt}, {@code LastSession.loggedInAt})
 * deserialise correctly.</p>
 */
public final class HttpResponseExtractor {

    private HttpResponseExtractor() {
        throw new IllegalStateException("Utility class");
    }

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    /**
     * Converts the raw {@code Object} received from
     * {@link com.ziminpro.twitter.services.UMSConnector} into a typed DTO.
     *
     * @param <T>   the target DTO type
     * @param data  the raw response body (a {@code LinkedHashMap} at runtime)
     * @param clazz the class to convert the {@code "data"} value into
     * @return the deserialised DTO; never {@code null} when UMS returns a
     *         well-formed envelope
     */
    @SuppressWarnings("unchecked")
    public static <T> T extractDataFromHttpClientResponse(Object data, Class<T> clazz) {
        Map<String, Object> envelope = (Map<String, Object>) data;
        return MAPPER.convertValue(envelope.get("data"), clazz);
    }
}