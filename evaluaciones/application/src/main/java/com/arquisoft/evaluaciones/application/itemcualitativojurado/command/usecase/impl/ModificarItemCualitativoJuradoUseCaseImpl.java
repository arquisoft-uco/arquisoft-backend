package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.ItemCualitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.ModificarItemCualitativoJuradoUseCase;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.ModificarItemCualitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ModificacionItemCualitativoJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarItemCualitativoJuradoUseCaseImpl
        implements ModificarItemCualitativoJuradoUseCase {

    private final ItemCualitativoJuradoOutputPort itemCualitativoJuradoOutputPort;
    private final ItemCualitativoJuradoExisteFinder itemCualitativoJuradoExisteFinder;
    private final ModificarItemCualitativoJuradoValidator modificarItemCualitativoJuradoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionItemCualitativoJuradoDomain modificacion) {
        logger.info(
                Mensajes.obtener(ItemCualitativoJuradoKey.LOG_MODIFICANDO),
                modificacion.getItemCualitativoJurado());

        boolean existe = itemCualitativoJuradoExisteFinder.obtener(
                modificacion.getItemCualitativoJurado());

        logger.debug(Mensajes.obtener(ItemCualitativoJuradoKey.LOG_VERIFICACION_MODIFICAR), existe);
        modificarItemCualitativoJuradoValidator.validar(
                modificacion.getItemCualitativoJurado(), existe);

        itemCualitativoJuradoOutputPort.actualizarDescripcion(
                modificacion.getItemCualitativoJurado(), modificacion.getDescripcion());

        logger.info(
                Mensajes.obtener(ItemCualitativoJuradoKey.LOG_MODIFICADO),
                modificacion.getItemCualitativoJurado());
    }
}
