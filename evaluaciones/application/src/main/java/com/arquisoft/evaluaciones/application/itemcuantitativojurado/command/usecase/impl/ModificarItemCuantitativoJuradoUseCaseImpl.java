package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.ItemCuantitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.ModificarItemCuantitativoJuradoUseCase;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.ModificarItemCuantitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ModificacionItemCuantitativoJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarItemCuantitativoJuradoUseCaseImpl
        implements ModificarItemCuantitativoJuradoUseCase {

    private final ItemCuantitativoJuradoOutputPort itemCuantitativoJuradoOutputPort;
    private final ItemCuantitativoJuradoExisteFinder itemCuantitativoJuradoExisteFinder;
    private final ModificarItemCuantitativoJuradoValidator modificarItemCuantitativoJuradoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionItemCuantitativoJuradoDomain modificacion) {
        logger.info(
                ItemCuantitativoJuradoKey.LOG_MODIFICANDO,
                modificacion.getItemCuantitativoJurado());

        var existe = itemCuantitativoJuradoExisteFinder.obtener(
                modificacion.getItemCuantitativoJurado());

        logger.debug(ItemCuantitativoJuradoKey.LOG_VERIFICACION_MODIFICAR, existe);
        modificarItemCuantitativoJuradoValidator.validar(
                modificacion.getItemCuantitativoJurado(), existe);

        itemCuantitativoJuradoOutputPort.actualizarDescripcion(
                modificacion.getItemCuantitativoJurado(), modificacion.getDescripcion());

        logger.info(
                ItemCuantitativoJuradoKey.LOG_MODIFICADO,
                modificacion.getItemCuantitativoJurado());
    }
}
