package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity.ItemFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.mapper.ItemFichaPerfilJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemFichaPerfilCommandOutputAdapter implements ItemFichaPerfilOutputPort {

    private final ItemFichaPerfilCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrarItem(ItemFichaPerfilEntity item) {
        repository.save(ItemFichaPerfilJpaMapper.toJpaEntity(item));
        logger.debug(ItemFichaPerfilKey.LOG_GUARDADO, item.id());
    }

    @Override
    public void actualizarContenido(UUID item, String contenido) {
        repository.actualizarContenido(item, contenido);
        logger.debug(ItemFichaPerfilKey.LOG_GUARDADO, item);
    }

    @Override
    public boolean existePorFichaYTipoItem(UUID fichaPerfilId, String tipoItem) {
        return repository.existsByFichaPerfilIdAndTipoItemId(fichaPerfilId, tipoItem);
    }

    @Override
    public boolean existePorId(UUID itemId) {
        return repository.existsById(itemId);
    }

    @Override
    public Optional<UUID> obtenerFichaPerfilId(UUID itemId) {
        return repository.obtenerFichaPerfilId(itemId);
    }

    @Override
    public void removerItem(UUID itemId) {
        repository.deleteById(itemId);
        logger.debug(ItemFichaPerfilKey.LOG_ELIMINADO, itemId);
    }
}
