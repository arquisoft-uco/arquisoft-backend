package com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.ConteoEvaluacionesPorEstadoEntity;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EstadoFichaDeEvaluacionEntity;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionExistenteEntity;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluacionFichaPerfilOutputPort {

    void registrarEvaluacion(EvaluacionFichaPerfilEntity evaluacion);

    boolean existePorId(UUID id);

    Optional<EvaluacionExistenteEntity> obtenerEvaluacionDeRepresentanteEnFicha(UUID representanteComiteId, UUID fichaPerfilId);

    boolean esRepresentantePropietario(UUID evaluacionFichaPerfil, UUID representanteComite);

    Optional<EstadoFichaDeEvaluacionEntity> obtenerEstadoFichaActual(UUID evaluacionFichaPerfilId);

    List<ConteoEvaluacionesPorEstadoEntity> contarEvaluacionesDeFichaPorEstadoEvaluacionActual(UUID fichaPerfil);
}
