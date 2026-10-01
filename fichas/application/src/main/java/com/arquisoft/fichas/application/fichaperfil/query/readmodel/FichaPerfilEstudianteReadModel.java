package com.arquisoft.fichas.application.fichaperfil.query.readmodel;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;

import java.util.List;
import java.util.UUID;

public record FichaPerfilEstudianteReadModel(
        UUID id,
        String tituloProyecto,
        AsesorFichaReadModel asesorFicha,
        EstadoFichaPerfilReadModel estado,
        List<EstudianteFichaPerfilReadModel> estudiantes
) {
}
