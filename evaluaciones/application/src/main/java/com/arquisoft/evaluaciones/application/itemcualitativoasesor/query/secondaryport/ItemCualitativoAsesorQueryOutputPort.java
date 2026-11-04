package com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;

import java.util.List;

public interface ItemCualitativoAsesorQueryOutputPort {

    List<ItemCualitativoAsesorReadModel> consultarTodos();
}
