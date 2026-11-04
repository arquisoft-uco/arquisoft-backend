package com.arquisoft.fichas.application.estadoficha.query.usecase.impl;

import com.arquisoft.fichas.application.estadoficha.query.criteria.EstadoFichaCriteria;
import com.arquisoft.fichas.application.estadoficha.query.finder.RolesNegocioActorQueryFinder;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.application.estadoficha.query.secondaryport.EstadoFichaQueryOutputPort;
import com.arquisoft.fichas.application.estadoficha.query.usecase.ConsultarEstadosFichaUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoFichaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosFichaUseCaseImpl implements ConsultarEstadosFichaUseCase {

    private final EstadoFichaQueryOutputPort queryOutputPort;
    private final RolesNegocioActorQueryFinder rolesNegocioActorQueryFinder;
    private final AppLogger logger;

    @Override
    public List<EstadoFichaReadModel> ejecutar(EstadoFichaCriteria criteria) {
        logger.debug(EstadoFichaKey.LOG_CONSULTANDO_ACTOR, criteria.actor(), criteria.rolesIdentidad());

        var rolesActor = rolesNegocioActorQueryFinder.obtener(criteria.actor());
        var roles = Stream.concat(criteria.rolesIdentidad().stream(), rolesActor.stream()).distinct().toList();
        logger.debug(EstadoFichaKey.LOG_ROLES_RESUELTOS, roles);

        var resultado = queryOutputPort.consultarPorRoles(roles);

        logger.debug(EstadoFichaKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
