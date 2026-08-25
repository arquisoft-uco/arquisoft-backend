package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.entity.ItemCualitativoJuradoEntity;

public interface ItemCualitativoJuradoOutputPort {

    void registrar(ItemCualitativoJuradoEntity entity);

    Boolean existePorNombreIgnorandoMayusculas(String nombre);
}
