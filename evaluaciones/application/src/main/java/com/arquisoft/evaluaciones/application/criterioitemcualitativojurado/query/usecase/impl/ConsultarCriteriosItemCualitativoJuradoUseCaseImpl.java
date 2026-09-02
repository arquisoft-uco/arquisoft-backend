package com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.secondaryport.CriterioItemCualitativoJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.usecase.ConsultarCriteriosItemCualitativoJuradoUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.evaluaciones.CriterioItemCualitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCriteriosItemCualitativoJuradoUseCaseImpl
        implements ConsultarCriteriosItemCualitativoJuradoUseCase {

    private final CriterioItemCualitativoJuradoQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<CriterioItemCualitativoJuradoReadModel> ejecutar(Void entrada) {
        var resultado = queryOutputPort.consultarTodos();

        logger.debug(Mensajes.obtener(CriterioItemCualitativoJuradoKey.LOG_CONSULTA_COMPLETADA), resultado.size());

        return resultado;
    }
}
