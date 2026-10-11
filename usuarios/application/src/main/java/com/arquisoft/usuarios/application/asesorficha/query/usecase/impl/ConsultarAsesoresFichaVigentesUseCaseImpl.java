package com.arquisoft.usuarios.application.asesorficha.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarAsesoresFichaVigentesKey;
import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaVigenteCriteria;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaVigenteReadModel;
import com.arquisoft.usuarios.application.asesorficha.query.secondaryport.AsesorFichaQueryOutputPort;
import com.arquisoft.usuarios.application.asesorficha.query.usecase.ConsultarAsesoresFichaVigentesUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresFichaVigentesUseCaseImpl implements ConsultarAsesoresFichaVigentesUseCase {

    private final AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<AsesorFichaVigenteReadModel> ejecutar(AsesorFichaVigenteCriteria entrada) {
        logger.debug(ConsultarAsesoresFichaVigentesKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = asesorFichaQueryOutputPort.consultarVigentes(entrada);

        logger.debug(ConsultarAsesoresFichaVigentesKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
