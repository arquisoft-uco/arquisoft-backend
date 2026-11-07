package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.command.finder.CategoriaItemCuantitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.NombreItemCuantitativoAsesorPorCategoriaExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.mapper.ItemCuantitativoAsesorMapper;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.RegistrarItemCuantitativoAsesorUseCase;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.RegistrarItemCuantitativoAsesorValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarItemCuantitativoAsesorUseCaseImpl
        implements RegistrarItemCuantitativoAsesorUseCase {

    private final ItemCuantitativoAsesorOutputPort itemCuantitativoAsesorOutputPort;
    private final CategoriaItemCuantitativoAsesorExisteFinder categoriaItemCuantitativoAsesorExisteFinder;
    private final NombreItemCuantitativoAsesorPorCategoriaExisteFinder
            nombreItemCuantitativoAsesorPorCategoriaExisteFinder;
    private final RegistrarItemCuantitativoAsesorValidator registrarItemCuantitativoAsesorValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(ItemCuantitativoAsesorDomain item) {
        logger.info(ItemCuantitativoAsesorKey.LOG_REGISTRANDO, item.getNombre(), item.getCategoria());

        var categoriaExiste = categoriaItemCuantitativoAsesorExisteFinder.obtener(item.getCategoria());
        var nombreYaExiste = nombreItemCuantitativoAsesorPorCategoriaExisteFinder.obtener(item);

        logger.debug(ItemCuantitativoAsesorKey.LOG_VERIFICACION_REGISTRAR, categoriaExiste, nombreYaExiste);
        registrarItemCuantitativoAsesorValidator.validar(item, categoriaExiste, nombreYaExiste);
        itemCuantitativoAsesorOutputPort.registrar(ItemCuantitativoAsesorMapper.toEntity(item));
        logger.info(ItemCuantitativoAsesorKey.LOG_REGISTRADO, item.getId());

        return item.getId();
    }
}
