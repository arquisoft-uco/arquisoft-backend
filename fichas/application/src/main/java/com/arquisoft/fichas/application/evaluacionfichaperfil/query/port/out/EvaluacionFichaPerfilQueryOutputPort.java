package com.arquisoft.fichas.application.evaluacionfichaperfil.query.port.out;

import java.util.UUID;

public interface EvaluacionFichaPerfilQueryOutputPort {

    boolean existsById(UUID evaluacionFichaPerfilId);
}
