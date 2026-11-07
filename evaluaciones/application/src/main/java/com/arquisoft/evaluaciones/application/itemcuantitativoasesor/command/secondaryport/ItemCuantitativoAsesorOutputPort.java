package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.entity.ItemCuantitativoAsesorEntity;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ItemCuantitativoAsesorOutputPort {

    Map<UUID, Integer> consultarValoresMaximosPorIds(Set<UUID> ids);

    void registrar(ItemCuantitativoAsesorEntity entity);

    boolean existePorNombreYCategoriaIgnorandoMayusculas(String nombre, UUID categoriaId);

    boolean existePorId(UUID id);

    void actualizarDescripcion(UUID id, String descripcion);
}
