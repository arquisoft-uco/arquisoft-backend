package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;

import java.util.List;
import java.util.UUID;

public interface RegistrarFichaPerfilValidator {

    void validar(FichaPerfilAggregate ficha, List<UUID> estudiantes,
                 List<EstudianteFichaPerfilAggregate> relaciones);
}
