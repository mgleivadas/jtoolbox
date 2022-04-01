package com.brontoblocks;

import java.nio.ByteBuffer;
import java.util.UUID;

public final class UuidUtils {

    public static byte[] toByteArray(UUID uuid) {
        var buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    public static UUID fromByteArray(byte[] bytes) {
        var buffer = ByteBuffer.wrap(bytes);
        return new UUID(buffer.getLong(), buffer.getLong());
    }
}
