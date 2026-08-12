package com.arquisoft.shared.util;

import java.util.UUID;

public final class UtilUUID {

    private static final String DEFAULT_UUID_STRING = "00000000-0000-0000-0000-000000000000";

    private static final String UUID_RE =
            "[a-fA-F0-9]{8}([-][a-fA-F0-9]{4}){3}[-][a-fA-F0-9]{12}";

    private UtilUUID() {}

    public static String getDefaultAsString() {
        return DEFAULT_UUID_STRING;
    }

    public static UUID generateNewUUID() {
        return UUID.randomUUID();
    }

    public static boolean uuidStringIsValid(final String uuidValue) {
        return UtilText.matchPattern(uuidValue, UUID_RE);
    }

    public static UUID getDefaultUUID() {
        return generateUUIDFromString(DEFAULT_UUID_STRING);
    }

    public static UUID generateUUIDFromString(final String uuidValue) {
        return uuidStringIsValid(uuidValue) ? UUID.fromString(uuidValue) : null;
    }
}
