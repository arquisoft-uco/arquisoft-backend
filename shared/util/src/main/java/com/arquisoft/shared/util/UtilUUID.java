package com.arquisoft.shared.util;

import java.util.UUID;

public final class UtilUUID {

    private static final String UUID_POR_DEFECTO = "00000000-0000-0000-0000-000000000000";

    private static final String PATRON_UUID =
            "[a-fA-F0-9]{8}([-][a-fA-F0-9]{4}){3}[-][a-fA-F0-9]{12}";

    private UtilUUID() {}

    public static String obtenerPorDefectoComoTexto() {
        return UUID_POR_DEFECTO;
    }

    public static UUID generarNuevoUUID() {
        return UUID.randomUUID();
    }

    public static boolean uuidValido(final String uuid) {
        return UtilTexto.coincidePatron(uuid, PATRON_UUID);
    }

    public static UUID obtenerUUIDPorDefecto() {
        return generarUUIDDesdeTexto(UUID_POR_DEFECTO);
    }

    public static UUID generarUUIDDesdeTexto(final String uuid) {
        return uuidValido(uuid) ? UUID.fromString(uuid) : null;
    }
}
