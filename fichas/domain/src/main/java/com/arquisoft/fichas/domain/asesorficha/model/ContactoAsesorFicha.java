package com.arquisoft.fichas.domain.asesorficha.model;

import java.util.UUID;

/**
 * Datos de contacto del asesor necesarios para notificarlo.
 *
 * <p>El agregado solo guarda {@code asesorFichaId}; el nombre y el correo viven en la tabla
 * {@code asesor_ficha}. El caso de uso los lee por un puerto y los entrega aqui para que el
 * evento de dominio pueda cargarlos, evitando que quien lo consuma tenga que consultar a fichas.
 *
 * @param id     identificador del asesor
 * @param nombre nombre visible del asesor
 * @param email  correo institucional del asesor
 */
public record ContactoAsesorFicha(UUID id, String nombre, String email) {
}
