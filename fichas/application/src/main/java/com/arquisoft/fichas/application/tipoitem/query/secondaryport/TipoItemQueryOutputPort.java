package com.arquisoft.fichas.application.tipoitem.query.secondaryport;

import com.arquisoft.fichas.application.tipoitem.query.readmodel.TipoItemReadModel;

import java.util.List;

public interface TipoItemQueryOutputPort {

    List<TipoItemReadModel> consultarTodos();
}
