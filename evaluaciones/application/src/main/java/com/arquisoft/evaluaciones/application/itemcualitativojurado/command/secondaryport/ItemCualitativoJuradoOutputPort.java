package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.entity.ItemCualitativoJuradoEntity;

import java.util.UUID;

public interface ItemCualitativoJuradoOutputPort {

    void registrar(ItemCualitativoJuradoEntity entity);

    boolean existePorNombreIgnorandoMayusculas(String nombre);

    boolean existePorId(UUID id);

    void actualizarDescripcion(UUID id, String descripcion);
}
