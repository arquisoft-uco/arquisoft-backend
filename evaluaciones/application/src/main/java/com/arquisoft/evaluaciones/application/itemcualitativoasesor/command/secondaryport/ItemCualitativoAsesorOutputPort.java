package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.entity.ItemCualitativoAsesorEntity;

import java.util.Set;
import java.util.UUID;

public interface ItemCualitativoAsesorOutputPort {

    Set<UUID> consultarIdsExistentes(Set<UUID> ids);

    void registrar(ItemCualitativoAsesorEntity entity);

    boolean existePorNombreIgnorandoMayusculas(String nombre);

    boolean existePorId(UUID id);

    void actualizarDescripcion(UUID id, String descripcion);

    void eliminar(UUID id);
}
