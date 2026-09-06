package com.arquisoft.fichas.application.estudiantefichaperfil.query.secondaryport;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;

import java.util.List;
import java.util.UUID;

public interface EstudianteFichaPerfilQueryOutputPort {

    List<EstudianteFichaPerfilReadModel> consultarPorFicha(UUID fichaPerfil);
}
