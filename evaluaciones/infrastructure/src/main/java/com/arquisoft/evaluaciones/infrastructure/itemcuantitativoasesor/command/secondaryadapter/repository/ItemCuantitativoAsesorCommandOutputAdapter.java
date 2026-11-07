package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.entity.ItemCuantitativoAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.mapper.ItemCuantitativoAsesorJpaMapper;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.secondaryadapter.projection.ValorMaximoItemCuantitativoAsesorProjection;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemCuantitativoAsesorCommandOutputAdapter implements ItemCuantitativoAsesorOutputPort {

    private final ItemCuantitativoAsesorCommandRepository repository;
    private final AppLogger logger;

    @Override
    public Map<UUID, Integer> consultarValoresMaximosPorIds(Set<UUID> ids) {
        return repository.findValoresMaximosByIdIn(ids).stream()
                .collect(Collectors.toUnmodifiableMap(
                        ValorMaximoItemCuantitativoAsesorProjection::getId,
                        ValorMaximoItemCuantitativoAsesorProjection::getValor));
    }

    @Override
    public void registrar(ItemCuantitativoAsesorEntity entity) {
        repository.save(ItemCuantitativoAsesorJpaMapper.toJpaEntity(entity));
        logger.debug(ItemCuantitativoAsesorKey.LOG_GUARDADO, entity.id());
    }

    @Override
    public boolean existePorNombreYCategoriaIgnorandoMayusculas(String nombre, UUID categoriaId) {
        return repository.existsByNombreIgnoreCaseAndCategoria(nombre, categoriaId);
    }

    @Override
    public boolean existePorId(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public void actualizarDescripcion(UUID id, String descripcion) {
        repository.actualizarDescripcion(id, descripcion);
        logger.debug(ItemCuantitativoAsesorKey.LOG_DESCRIPCION_ACTUALIZADA, id);
    }
}
