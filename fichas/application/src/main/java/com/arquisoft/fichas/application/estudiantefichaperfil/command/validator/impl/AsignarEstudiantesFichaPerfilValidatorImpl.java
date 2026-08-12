package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.AsignarEstudiantesFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiante.model.ExistenciaEstudiantes;
import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.CupoEstudiantesFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculosEstudiantesFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudianteFichaPerfilCupoDisponibleRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesNoVinculadosRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesSinDuplicadosRule;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarEstudiantesFichaPerfilValidatorImpl implements AsignarEstudiantesFichaPerfilValidator {

    private final EstudiantesSinDuplicadosRule estudiantesSinDuplicadosRule;
    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final EstudiantesExistenRule estudiantesExistenRule;
    private final EstudiantesNoVinculadosRule estudiantesNoVinculadosRule;
    private final EstudianteFichaPerfilCupoDisponibleRule estudianteFichaPerfilCupoDisponibleRule;

    @Override
    public void validar(List<EstudianteFichaPerfilDomain> relaciones, boolean fichaExiste,
                        List<UUID> estudiantesExistentes, List<UUID> yaVinculados, long vinculadosActuales) {

        UUID fichaPerfil = relaciones.getFirst().getFichaPerfilId();
        List<UUID> estudiantes = relaciones.stream().map(EstudianteFichaPerfilDomain::getEstudianteId).toList();

        estudiantesSinDuplicadosRule.validar(estudiantes);

        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(fichaPerfil, fichaExiste));
        estudiantesExistenRule.validar(new ExistenciaEstudiantes(estudiantes, estudiantesExistentes));
        estudiantesNoVinculadosRule.validar(new VinculosEstudiantesFicha(yaVinculados));

        estudianteFichaPerfilCupoDisponibleRule.validar(
                new CupoEstudiantesFicha(vinculadosActuales, relaciones.size()));
    }
}
