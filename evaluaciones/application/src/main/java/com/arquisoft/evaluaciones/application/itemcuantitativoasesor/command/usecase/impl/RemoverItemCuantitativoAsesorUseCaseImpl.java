package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.EvaluacionCuantitativaAsesorPorItemExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.ItemCuantitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.RemoverItemCuantitativoAsesorUseCase;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.RemoverItemCuantitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.RemocionItemCuantitativoAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverItemCuantitativoAsesorUseCaseImpl
        implements RemoverItemCuantitativoAsesorUseCase {

    private final ItemCuantitativoAsesorOutputPort itemCuantitativoAsesorOutputPort;
    private final ItemCuantitativoAsesorExisteFinder itemCuantitativoAsesorExisteFinder;
    private final EvaluacionCuantitativaAsesorPorItemExisteFinder evaluacionCuantitativaAsesorPorItemExisteFinder;
    private final RemoverItemCuantitativoAsesorValidator removerItemCuantitativoAsesorValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionItemCuantitativoAsesorDomain remocion) {
        logger.info(
                ItemCuantitativoAsesorKey.LOG_REMOVIENDO,
                remocion.getItemCuantitativoAsesor());

        var existe = itemCuantitativoAsesorExisteFinder.obtener(
                remocion.getItemCuantitativoAsesor());
        var enUso = evaluacionCuantitativaAsesorPorItemExisteFinder.obtener(
                remocion.getItemCuantitativoAsesor());

        logger.debug(ItemCuantitativoAsesorKey.LOG_VERIFICACION_REMOVER, existe, enUso);
        removerItemCuantitativoAsesorValidator.validar(
                remocion.getItemCuantitativoAsesor(), existe, enUso);

        itemCuantitativoAsesorOutputPort.eliminar(remocion.getItemCuantitativoAsesor());

        logger.info(
                ItemCuantitativoAsesorKey.LOG_REMOVIDO,
                remocion.getItemCuantitativoAsesor());
    }
}
