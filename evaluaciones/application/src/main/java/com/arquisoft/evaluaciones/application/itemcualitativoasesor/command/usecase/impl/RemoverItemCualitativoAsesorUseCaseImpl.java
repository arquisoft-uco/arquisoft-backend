package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.EvaluacionCualitativaAsesorPorItemExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.ItemCualitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.RemoverItemCualitativoAsesorUseCase;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.RemoverItemCualitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.RemocionItemCualitativoAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverItemCualitativoAsesorUseCaseImpl
        implements RemoverItemCualitativoAsesorUseCase {

    private final ItemCualitativoAsesorOutputPort itemCualitativoAsesorOutputPort;
    private final ItemCualitativoAsesorExisteFinder itemCualitativoAsesorExisteFinder;
    private final EvaluacionCualitativaAsesorPorItemExisteFinder evaluacionCualitativaAsesorPorItemExisteFinder;
    private final RemoverItemCualitativoAsesorValidator removerItemCualitativoAsesorValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionItemCualitativoAsesorDomain remocion) {
        logger.info(
                ItemCualitativoAsesorKey.LOG_REMOVIENDO,
                remocion.getItemCualitativoAsesor());

        var existe = itemCualitativoAsesorExisteFinder.obtener(
                remocion.getItemCualitativoAsesor());
        var enUso = evaluacionCualitativaAsesorPorItemExisteFinder.obtener(
                remocion.getItemCualitativoAsesor());

        logger.debug(ItemCualitativoAsesorKey.LOG_VERIFICACION_REMOVER, existe, enUso);
        removerItemCualitativoAsesorValidator.validar(
                remocion.getItemCualitativoAsesor(), existe, enUso);

        itemCualitativoAsesorOutputPort.eliminar(remocion.getItemCualitativoAsesor());

        logger.info(
                ItemCualitativoAsesorKey.LOG_REMOVIDO,
                remocion.getItemCualitativoAsesor());
    }
}
