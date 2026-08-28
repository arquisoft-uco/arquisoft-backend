package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.NombreItemCualitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.mapper.ItemCualitativoJuradoMapper;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.usecase.RegistrarItemCualitativoJuradoUseCase;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.RegistrarItemCualitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ItemCualitativoJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarItemCualitativoJuradoUseCaseImpl
        implements RegistrarItemCualitativoJuradoUseCase {

    private final ItemCualitativoJuradoOutputPort itemCualitativoJuradoOutputPort;
    private final NombreItemCualitativoJuradoExisteFinder nombreItemCualitativoJuradoExisteFinder;
    private final RegistrarItemCualitativoJuradoValidator registrarItemCualitativoJuradoValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(ItemCualitativoJuradoDomain item) {
        logger.info(Mensajes.obtener(ItemCualitativoJuradoKey.LOG_REGISTRANDO), item.getNombre());

        var nombreYaExiste = nombreItemCualitativoJuradoExisteFinder.obtener(item.getNombre());

        logger.debug(Mensajes.obtener(ItemCualitativoJuradoKey.LOG_VERIFICACION_REGISTRAR), nombreYaExiste);
        registrarItemCualitativoJuradoValidator.validar(item, nombreYaExiste);
        itemCualitativoJuradoOutputPort.registrar(ItemCualitativoJuradoMapper.toEntity(item));
        logger.info(Mensajes.obtener(ItemCualitativoJuradoKey.LOG_REGISTRADO), item.getId());

        return item.getId();
    }
}
