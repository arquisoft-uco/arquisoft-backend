package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.CategoriaItemCuantitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.NombreItemCuantitativoJuradoPorCategoriaExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.mapper.ItemCuantitativoJuradoMapper;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.RegistrarItemCuantitativoJuradoUseCase;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.RegistrarItemCuantitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarItemCuantitativoJuradoUseCaseImpl
        implements RegistrarItemCuantitativoJuradoUseCase {

    private final ItemCuantitativoJuradoOutputPort itemCuantitativoJuradoOutputPort;
    private final CategoriaItemCuantitativoJuradoExisteFinder categoriaExisteFinder;
    private final NombreItemCuantitativoJuradoPorCategoriaExisteFinder nombreExisteFinder;
    private final RegistrarItemCuantitativoJuradoValidator validator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(ItemCuantitativoJuradoDomain item) {
        logger.info(
                ItemCuantitativoJuradoKey.LOG_REGISTRANDO,
                item.getNombre(),
                item.getCategoria());

        boolean categoriaExiste = categoriaExisteFinder.obtener(item.getCategoria());
        boolean nombreYaExiste = nombreExisteFinder.obtener(item);

        logger.debug(
                ItemCuantitativoJuradoKey.LOG_VERIFICACION_REGISTRAR,
                categoriaExiste,
                nombreYaExiste);
        validator.validar(item, categoriaExiste, nombreYaExiste);

        itemCuantitativoJuradoOutputPort.registrar(ItemCuantitativoJuradoMapper.toEntity(item));
        logger.info(ItemCuantitativoJuradoKey.LOG_REGISTRADO, item.getId());

        return item.getId();
    }
}
