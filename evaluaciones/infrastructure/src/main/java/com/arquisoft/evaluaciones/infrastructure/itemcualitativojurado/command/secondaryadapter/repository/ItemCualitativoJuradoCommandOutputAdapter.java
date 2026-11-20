package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.entity.ItemCualitativoJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.mapper.ItemCualitativoJuradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemCualitativoJuradoCommandOutputAdapter
        implements ItemCualitativoJuradoOutputPort {

    private final ItemCualitativoJuradoCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrar(ItemCualitativoJuradoEntity entity) {
        repository.save(ItemCualitativoJuradoJpaMapper.toJpaEntity(entity));
        logger.debug(ItemCualitativoJuradoKey.LOG_GUARDADA, entity.id());
    }

    @Override
    public boolean existePorNombreIgnorandoMayusculas(String nombre) {
        return repository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    public boolean existePorId(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public void actualizar(UUID id, String descripcion, int peso) {
        repository.actualizar(id, descripcion, peso);
        logger.debug(ItemCualitativoJuradoKey.LOG_ACTUALIZADO, id);
    }

    @Override
    public void eliminar(UUID id) {
        repository.deleteById(id);
        logger.debug(ItemCualitativoJuradoKey.LOG_ELIMINADO, id);
    }

    @Override
    public Set<UUID> consultarIdsExistentes(Set<UUID> ids) {
        return repository.findIdsByIdIn(ids);
    }
}
