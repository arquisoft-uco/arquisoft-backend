package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.entity.ItemCualitativoJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.mapper.ItemCualitativoJuradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
        logger.debug(Mensajes.obtener(ItemCualitativoJuradoKey.LOG_GUARDADA), entity.id());
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
        logger.debug(Mensajes.obtener(ItemCualitativoJuradoKey.LOG_DESCRIPCION_ACTUALIZADA), id);
    }
}
