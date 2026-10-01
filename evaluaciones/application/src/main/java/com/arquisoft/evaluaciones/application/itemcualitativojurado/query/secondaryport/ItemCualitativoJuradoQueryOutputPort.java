package com.arquisoft.evaluaciones.application.itemcualitativojurado.query.secondaryport;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;

import java.util.List;

public interface ItemCualitativoJuradoQueryOutputPort {

    List<ItemCualitativoJuradoReadModel> consultarTodos();
}
