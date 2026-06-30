package com.arquisoft.fichas.application.estudiantefichaperfil.query.port.out;

import java.util.UUID;

public interface EstudianteFichaPerfilQueryOutputPort {

    boolean existePorEstudianteYFicha(UUID estudianteId, UUID fichaPerfilId);
}
