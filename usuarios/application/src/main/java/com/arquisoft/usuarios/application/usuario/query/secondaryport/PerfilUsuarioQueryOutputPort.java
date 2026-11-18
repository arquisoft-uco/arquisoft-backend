package com.arquisoft.usuarios.application.usuario.query.secondaryport;

import com.arquisoft.usuarios.application.usuario.query.criteria.PerfilUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.PerfilUsuarioReadModel;

public interface PerfilUsuarioQueryOutputPort {

    PerfilUsuarioReadModel consultar(PerfilUsuarioCriteria criteria);
}
