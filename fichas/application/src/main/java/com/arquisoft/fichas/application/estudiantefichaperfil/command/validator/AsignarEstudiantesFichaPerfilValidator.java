package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculacionEstudiantesCriteria;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudianteFichaPerfilCupoDisponibleRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesNoVinculadosRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesSinDuplicadosRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarEstudiantesFichaPerfilValidator {

    private final EstudiantesSinDuplicadosRule estudiantesSinDuplicadosRule;
    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final EstudiantesExistenRule estudiantesExistenRule;
    private final EstudiantesNoVinculadosRule estudiantesNoVinculadosRule;
    private final EstudianteFichaPerfilCupoDisponibleRule estudianteFichaPerfilCupoDisponibleRule;

    public void validar(UUID fichaPerfil, List<UUID> estudiantes,
                        List<EstudianteFichaPerfilAggregate> relaciones) {

        estudiantesSinDuplicadosRule.validar(estudiantes);

        fichaPerfilExisteRule.validar(fichaPerfil);
        estudiantesExistenRule.validar(estudiantes);
        estudiantesNoVinculadosRule.validar(new VinculacionEstudiantesCriteria(fichaPerfil, estudiantes));

        estudianteFichaPerfilCupoDisponibleRule.validar(relaciones);
    }
}
