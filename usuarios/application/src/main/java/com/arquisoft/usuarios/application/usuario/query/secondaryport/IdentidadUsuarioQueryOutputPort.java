package com.arquisoft.usuarios.application.usuario.query.secondaryport;

import com.arquisoft.usuarios.application.usuario.query.criteria.IdentidadUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.IdentidadUsuarioReadModel;

public interface IdentidadUsuarioQueryOutputPort {

    IdentidadUsuarioReadModel consultar(IdentidadUsuarioCriteria criteria);
}
