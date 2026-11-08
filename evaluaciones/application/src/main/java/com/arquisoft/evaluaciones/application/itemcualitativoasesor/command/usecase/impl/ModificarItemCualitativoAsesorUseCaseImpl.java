package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.ItemCualitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.ModificarItemCualitativoAsesorUseCase;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.ModificarItemCualitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ModificacionItemCualitativoAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarItemCualitativoAsesorUseCaseImpl
        implements ModificarItemCualitativoAsesorUseCase {

    private final ItemCualitativoAsesorOutputPort itemCualitativoAsesorOutputPort;
    private final ItemCualitativoAsesorExisteFinder itemCualitativoAsesorExisteFinder;
    private final ModificarItemCualitativoAsesorValidator modificarItemCualitativoAsesorValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionItemCualitativoAsesorDomain modificacion) {
        logger.info(
                ItemCualitativoAsesorKey.LOG_MODIFICANDO,
                modificacion.getItemCualitativoAsesor());

        var existe = itemCualitativoAsesorExisteFinder.obtener(
                modificacion.getItemCualitativoAsesor());

        logger.debug(ItemCualitativoAsesorKey.LOG_VERIFICACION_MODIFICAR, existe);
        modificarItemCualitativoAsesorValidator.validar(
                modificacion.getItemCualitativoAsesor(), existe);

        itemCualitativoAsesorOutputPort.actualizarDescripcion(
                modificacion.getItemCualitativoAsesor(), modificacion.getDescripcion());

        logger.info(
                ItemCualitativoAsesorKey.LOG_MODIFICADO,
                modificacion.getItemCualitativoAsesor());
    }
}
