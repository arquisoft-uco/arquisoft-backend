package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity.ItemFichaPerfilEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemFichaPerfilCommandOutputAdapter implements ItemFichaPerfilOutputPort {

    private final ItemFichaPerfilCommandRepository repository;

    @Override
    public void registrarItem(ItemFichaPerfilEntity item) {
        repository.save(item);
    }

    @Override
    public void actualizarContenido(UUID item, String contenido) {
        repository.actualizarContenido(item, contenido);
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
    }
}
