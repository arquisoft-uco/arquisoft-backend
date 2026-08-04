package com.arquisoft.fichas.application.asesorficha.query.readmodel;

import java.util.UUID;

/**
 * Proyeccion plana con los datos de contacto de un asesor.
 *
 * @param id     identificador del asesor
 * @param nombre nombre visible
 * @param email  correo institucional
 */
public record AsesorContactoReadModel(UUID id, String nombre, String email) {
}
