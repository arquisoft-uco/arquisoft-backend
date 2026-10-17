package com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.secondaryport;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;

import java.util.List;

public interface ItemCuantitativoJuradoQueryOutputPort {

    List<ItemCuantitativoJuradoReadModel> consultarTodos();
}
