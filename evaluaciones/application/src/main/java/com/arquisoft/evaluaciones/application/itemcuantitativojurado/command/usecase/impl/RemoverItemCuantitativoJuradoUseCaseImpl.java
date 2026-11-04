package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.EvaluacionCuantitativaJuradoPorItemExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.ItemCuantitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.RemoverItemCuantitativoJuradoUseCase;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.RemoverItemCuantitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.RemocionItemCuantitativoJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverItemCuantitativoJuradoUseCaseImpl
        implements RemoverItemCuantitativoJuradoUseCase {

    private final ItemCuantitativoJuradoOutputPort itemCuantitativoJuradoOutputPort;
    private final ItemCuantitativoJuradoExisteFinder itemCuantitativoJuradoExisteFinder;
    private final EvaluacionCuantitativaJuradoPorItemExisteFinder evaluacionCuantitativaJuradoPorItemExisteFinder;
    private final RemoverItemCuantitativoJuradoValidator removerItemCuantitativoJuradoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionItemCuantitativoJuradoDomain remocion) {
        logger.info(
                ItemCuantitativoJuradoKey.LOG_REMOVIENDO,
                remocion.getItemCuantitativoJurado());

        var existe = itemCuantitativoJuradoExisteFinder.obtener(
                remocion.getItemCuantitativoJurado());
        var enUso = evaluacionCuantitativaJuradoPorItemExisteFinder.obtener(
                remocion.getItemCuantitativoJurado());

        logger.debug(ItemCuantitativoJuradoKey.LOG_VERIFICACION_REMOVER, existe, enUso);
        removerItemCuantitativoJuradoValidator.validar(
                remocion.getItemCuantitativoJurado(), existe, enUso);

        itemCuantitativoJuradoOutputPort.eliminar(remocion.getItemCuantitativoJurado());

        logger.info(
                ItemCuantitativoJuradoKey.LOG_REMOVIDO,
                remocion.getItemCuantitativoJurado());
    }
}
