package com.arquisoft.usuarios.application.estadousuario.query.secondaryport;

import com.arquisoft.usuarios.application.estadousuario.query.readmodel.EstadoUsuarioReadModel;

import java.util.List;

public interface EstadoUsuarioQueryOutputPort {

    List<EstadoUsuarioReadModel> consultarTodos();
}
