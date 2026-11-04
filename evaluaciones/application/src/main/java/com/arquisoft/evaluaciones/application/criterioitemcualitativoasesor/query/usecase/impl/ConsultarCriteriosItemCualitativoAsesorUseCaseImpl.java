package com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.secondaryport.CriterioItemCualitativoAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.usecase.ConsultarCriteriosItemCualitativoAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.CriterioItemCualitativoAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCriteriosItemCualitativoAsesorUseCaseImpl implements ConsultarCriteriosItemCualitativoAsesorUseCase {

    private final CriterioItemCualitativoAsesorQueryOutputPort queryOutputPort;
    private final AppLogger logger;

    @Override
    public List<CriterioItemCualitativoAsesorReadModel> ejecutar() {
        var resultado = queryOutputPort.consultarTodos();

        logger.debug(CriterioItemCualitativoAsesorKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
