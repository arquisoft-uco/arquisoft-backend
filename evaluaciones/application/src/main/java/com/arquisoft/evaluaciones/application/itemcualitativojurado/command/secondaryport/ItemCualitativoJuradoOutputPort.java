package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.entity.ItemCualitativoJuradoEntity;

import java.util.Set;
import java.util.UUID;

public interface ItemCualitativoJuradoOutputPort {

    void registrar(ItemCualitativoJuradoEntity entity);

    boolean existePorNombreIgnorandoMayusculas(String nombre);

    boolean existePorId(UUID id);

    void actualizar(UUID id, String descripcion, int peso);

    void eliminar(UUID id);

    Set<UUID> consultarIdsExistentes(Set<UUID> ids);
}
