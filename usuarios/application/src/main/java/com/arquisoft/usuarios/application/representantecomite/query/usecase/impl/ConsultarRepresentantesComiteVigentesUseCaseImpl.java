package com.arquisoft.usuarios.application.representantecomite.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarRepresentantesComiteVigentesKey;
import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteVigenteCriteria;
import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteVigenteReadModel;
import com.arquisoft.usuarios.application.representantecomite.query.secondaryport.RepresentanteComiteQueryOutputPort;
import com.arquisoft.usuarios.application.representantecomite.query.usecase.ConsultarRepresentantesComiteVigentesUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarRepresentantesComiteVigentesUseCaseImpl implements ConsultarRepresentantesComiteVigentesUseCase {

    private final RepresentanteComiteQueryOutputPort representanteComiteQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<RepresentanteComiteVigenteReadModel> ejecutar(RepresentanteComiteVigenteCriteria entrada) {
        logger.debug(ConsultarRepresentantesComiteVigentesKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = representanteComiteQueryOutputPort.consultarVigentes(entrada);

        logger.debug(ConsultarRepresentantesComiteVigentesKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
