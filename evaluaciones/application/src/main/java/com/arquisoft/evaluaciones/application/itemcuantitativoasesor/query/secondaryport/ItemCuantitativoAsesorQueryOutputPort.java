package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.secondaryport;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;

import java.util.List;

public interface ItemCuantitativoAsesorQueryOutputPort {

    List<ItemCuantitativoAsesorReadModel> consultarTodos();
}
