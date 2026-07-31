package com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria;

import java.util.UUID;

/**
 * Criterio de verificación de propiedad de una evaluación de ficha de perfil
 * por un representante del comité de currículum.
 */
public record PropietarioEvaluacionCriteria(UUID evaluacionFichaPerfil, UUID representanteComite) {
}
