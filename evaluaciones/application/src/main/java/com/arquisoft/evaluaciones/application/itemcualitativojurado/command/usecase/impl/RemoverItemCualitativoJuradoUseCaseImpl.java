package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.ItemCualitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.RemoverItemCualitativoJuradoUseCase;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.RemoverItemCualitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.RemocionItemCualitativoJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverItemCualitativoJuradoUseCaseImpl
        implements RemoverItemCualitativoJuradoUseCase {

    private final ItemCualitativoJuradoOutputPort itemCualitativoJuradoOutputPort;
    private final ItemCualitativoJuradoExisteFinder itemCualitativoJuradoExisteFinder;
    private final RemoverItemCualitativoJuradoValidator removerItemCualitativoJuradoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionItemCualitativoJuradoDomain remocion) {
        logger.info(
                ItemCualitativoJuradoKey.LOG_REMOVIENDO,
                remocion.getItemCualitativoJurado());

        var existe = itemCualitativoJuradoExisteFinder.obtener(
                remocion.getItemCualitativoJurado());

        logger.debug(ItemCualitativoJuradoKey.LOG_VERIFICACION_REMOVER, existe);
        removerItemCualitativoJuradoValidator.validar(
                remocion.getItemCualitativoJurado(), existe);

        itemCualitativoJuradoOutputPort.marcarEliminado(
                remocion.getItemCualitativoJurado(), remocion.getEliminadoEn());

        logger.info(
                ItemCualitativoJuradoKey.LOG_REMOVIDO,
                remocion.getItemCualitativoJurado());
    }
}
