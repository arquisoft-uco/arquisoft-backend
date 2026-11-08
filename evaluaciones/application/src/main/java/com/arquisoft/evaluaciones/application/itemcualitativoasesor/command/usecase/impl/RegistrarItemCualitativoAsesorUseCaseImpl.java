package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.NombreItemCualitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.mapper.ItemCualitativoAsesorMapper;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.RegistrarItemCualitativoAsesorUseCase;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.RegistrarItemCualitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ItemCualitativoAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarItemCualitativoAsesorUseCaseImpl implements RegistrarItemCualitativoAsesorUseCase {

    private final ItemCualitativoAsesorOutputPort itemCualitativoAsesorOutputPort;
    private final NombreItemCualitativoAsesorExisteFinder nombreItemCualitativoAsesorExisteFinder;
    private final RegistrarItemCualitativoAsesorValidator registrarItemCualitativoAsesorValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(ItemCualitativoAsesorDomain item) {
        logger.info(ItemCualitativoAsesorKey.LOG_REGISTRANDO, item.getNombre());

        var nombreYaExiste = nombreItemCualitativoAsesorExisteFinder.obtener(item.getNombre());

        logger.debug(ItemCualitativoAsesorKey.LOG_VERIFICACION_REGISTRAR, nombreYaExiste);
        registrarItemCualitativoAsesorValidator.validar(item, nombreYaExiste);
        itemCualitativoAsesorOutputPort.registrar(ItemCualitativoAsesorMapper.toEntity(item));
        logger.info(ItemCualitativoAsesorKey.LOG_REGISTRADO, item.getId());

        return item.getId();
    }
}
