package com.arquisoft.shared.redis.catalogo.messageuco;

import java.util.List;

/**
 * Lo que dejó una sincronización.
 *
 * @param actualizadas claves cuyo texto cambió en Redis
 * @param ausentes     claves declaradas que MessageUcoLab no trajo; conservan su texto en Redis
 * @param rechazadas   claves con texto vacío o aridad distinta a la declarada; no se escriben
 * @param ignorados    mensajes cuyo título no es una clave declarada; no se escriben
 */
public record ResultadoSincronizacion(
        int actualizadas, List<String> ausentes, List<String> rechazadas, int ignorados) {
}
