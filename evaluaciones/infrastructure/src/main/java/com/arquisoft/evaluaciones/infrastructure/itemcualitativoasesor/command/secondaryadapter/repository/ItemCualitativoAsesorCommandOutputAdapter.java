package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.entity.ItemCualitativoAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.secondaryadapter.mapper.ItemCualitativoAsesorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemCualitativoAsesorCommandOutputAdapter implements ItemCualitativoAsesorOutputPort {

    private final ItemCualitativoAsesorCommandRepository repository;
    private final AppLogger logger;

    @Override
    public Set<UUID> consultarIdsExistentes(Set<UUID> ids) {
        return repository.findIdsByIdIn(ids);
    }

    @Override
    public void registrar(ItemCualitativoAsesorEntity entity) {
        repository.save(ItemCualitativoAsesorJpaMapper.toJpaEntity(entity));
        logger.debug(ItemCualitativoAsesorKey.LOG_GUARDADA, entity.id());
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
    public void actualizarDescripcion(UUID id, String descripcion) {
        repository.actualizarDescripcion(id, descripcion);
        logger.debug(ItemCualitativoAsesorKey.LOG_DESCRIPCION_ACTUALIZADA, id);
    }
}
