package com.arquisoft.usuarios.application.jurado.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarJuradosVigentesKey;
import com.arquisoft.usuarios.application.jurado.query.criteria.JuradoVigenteCriteria;
import com.arquisoft.usuarios.application.jurado.query.readmodel.JuradoVigenteReadModel;
import com.arquisoft.usuarios.application.jurado.query.secondaryport.JuradoQueryOutputPort;
import com.arquisoft.usuarios.application.jurado.query.usecase.ConsultarJuradosVigentesUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarJuradosVigentesUseCaseImpl implements ConsultarJuradosVigentesUseCase {

    private final JuradoQueryOutputPort juradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<JuradoVigenteReadModel> ejecutar(JuradoVigenteCriteria entrada) {
        logger.debug(ConsultarJuradosVigentesKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = juradoQueryOutputPort.consultarVigentes(entrada);

        logger.debug(ConsultarJuradosVigentesKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
