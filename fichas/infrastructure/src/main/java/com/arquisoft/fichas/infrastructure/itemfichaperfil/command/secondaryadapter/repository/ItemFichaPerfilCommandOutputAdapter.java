package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence.ItemFichaPerfilRepository;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence.ItemFichaPerfilMapper;
import com.arquisoft.fichas.infrastructure.tipoitem.persistence.TipoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemFichaPerfilCommandOutputAdapter implements ItemFichaPerfilOutputPort {

    private final ItemFichaPerfilRepository repository;
    private final TipoItemRepository tipoItemRepository;

    @Override
    public void registrarItem(ItemFichaPerfilDomain item) {
        var tipoItemRef = tipoItemRepository.getReferenceById(item.getTipoItem().getId());
        repository.save(ItemFichaPerfilMapper.toEntity(item, tipoItemRef));
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
