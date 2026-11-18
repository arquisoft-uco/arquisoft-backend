package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.ItemCuantitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.ModificarItemCuantitativoAsesorUseCase;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.ModificarItemCuantitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ModificacionItemCuantitativoAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarItemCuantitativoAsesorUseCaseImpl
        implements ModificarItemCuantitativoAsesorUseCase {

    private final ItemCuantitativoAsesorOutputPort itemCuantitativoAsesorOutputPort;
    private final ItemCuantitativoAsesorExisteFinder itemCuantitativoAsesorExisteFinder;
    private final ModificarItemCuantitativoAsesorValidator modificarItemCuantitativoAsesorValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionItemCuantitativoAsesorDomain modificacion) {
        logger.info(
                ItemCuantitativoAsesorKey.LOG_MODIFICANDO,
                modificacion.getItemCuantitativoAsesor());

        var existe = itemCuantitativoAsesorExisteFinder.obtener(
                modificacion.getItemCuantitativoAsesor());

        logger.debug(ItemCuantitativoAsesorKey.LOG_VERIFICACION_MODIFICAR, existe);
        modificarItemCuantitativoAsesorValidator.validar(
                modificacion.getItemCuantitativoAsesor(), existe);

        itemCuantitativoAsesorOutputPort.actualizarDescripcion(
                modificacion.getItemCuantitativoAsesor(), modificacion.getDescripcion());

        logger.info(
                ItemCuantitativoAsesorKey.LOG_MODIFICADO,
                modificacion.getItemCuantitativoAsesor());
    }
}
