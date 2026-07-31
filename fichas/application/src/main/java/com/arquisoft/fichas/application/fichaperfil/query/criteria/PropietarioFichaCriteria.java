package com.arquisoft.fichas.application.fichaperfil.query.criteria;

import java.util.UUID;

/**
 * Criterio de verificación de propiedad de una ficha de perfil por un estudiante.
 *
 * <p>Evita firmas con parámetros sueltos del mismo tipo — donde invertir los
 * argumentos compila sin error — y da nombre de negocio a lo que se consulta.</p>
 */
public record PropietarioFichaCriteria(UUID fichaPerfil, UUID estudiante) {
}
