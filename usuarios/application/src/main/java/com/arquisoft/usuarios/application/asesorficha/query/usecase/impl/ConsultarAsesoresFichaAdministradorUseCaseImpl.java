package com.arquisoft.usuarios.application.asesorficha.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarAsesoresFichaAdministradorKey;
import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaCriteria;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.usuarios.application.asesorficha.query.secondaryport.AsesorFichaQueryOutputPort;
import com.arquisoft.usuarios.application.asesorficha.query.usecase.ConsultarAsesoresFichaAdministradorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresFichaAdministradorUseCaseImpl implements ConsultarAsesoresFichaAdministradorUseCase {

    private final AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<AsesorFichaReadModel> ejecutar(AsesorFichaCriteria entrada) {
        logger.debug(ConsultarAsesoresFichaAdministradorKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = asesorFichaQueryOutputPort.consultarTodos(entrada);

        logger.debug(ConsultarAsesoresFichaAdministradorKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
