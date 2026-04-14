package com.ziminpro.twitter.dao;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Low-level conversion utilities shared by all JDBC repositories.
 *
 * <p>MySQL stores UUIDs as {@code BINARY(16)} (via {@code UUID_TO_BIN()}).
 * These helpers convert between that byte representation and Java's
 * {@link UUID}, preserving the most-significant / least-significant bit
 * ordering that {@link ByteBuffer} uses.</p>
 */
public final class DaoHelper {

    private DaoHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static UUID bytesArrayToUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong  = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static byte[] uuidToBytesArray(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}