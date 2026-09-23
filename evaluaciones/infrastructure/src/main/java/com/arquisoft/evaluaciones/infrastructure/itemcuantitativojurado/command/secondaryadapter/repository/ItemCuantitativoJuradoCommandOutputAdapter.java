package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.entity.ItemCuantitativoJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.mapper.ItemCuantitativoJuradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemCuantitativoJuradoCommandOutputAdapter
        implements ItemCuantitativoJuradoOutputPort {

    private final ItemCuantitativoJuradoCommandRepository repository;
    private final CategoriaItemCuantitativoJuradoCommandRepository categoriaRepository;
    private final AppLogger logger;

    @Override
    public void registrar(ItemCuantitativoJuradoEntity item) {
        repository.save(ItemCuantitativoJuradoJpaMapper.toJpaEntity(item));
        logger.debug(ItemCuantitativoJuradoKey.LOG_GUARDADO, item.id());
    }

    @Override
    public boolean existeCategoriaPorId(UUID categoriaId) {
        return categoriaRepository.existsById(categoriaId);
    }

    @Override
    public boolean existePorNombreYCategoriaIgnorandoMayusculas(
            String nombre, UUID categoriaId) {
        return repository.existsByNombreIgnoreCaseAndCategoriaId(nombre, categoriaId);
    }
}
