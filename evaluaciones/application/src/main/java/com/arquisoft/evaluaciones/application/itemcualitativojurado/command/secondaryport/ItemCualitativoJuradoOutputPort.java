package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.entity.ItemCualitativoJuradoEntity;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public interface ItemCualitativoJuradoOutputPort {

    void registrar(ItemCualitativoJuradoEntity entity);

    boolean existePorNombreIgnorandoMayusculas(String nombre);

    boolean existePorId(UUID id);

    void actualizarDescripcion(UUID id, String descripcion);

    void marcarEliminado(UUID id, Instant eliminadoEn);

    Set<UUID> consultarIdsExistentes(Set<UUID> ids);
}
