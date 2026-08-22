package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.AsignarEstudiantesFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiante.model.ExistenciaEstudiantes;
import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiante.rules.impl.EstudiantesExistenRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.CupoEstudiantesFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculosEstudiantesFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudianteFichaPerfilCupoDisponibleRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudianteFichaPerfilCupoDisponibleRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesNoVinculadosRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudiantesNoVinculadosRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesSinDuplicadosRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudiantesSinDuplicadosRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AsignarEstudiantesFichaPerfilValidatorImpl implements AsignarEstudiantesFichaPerfilValidator {

    private final EstudiantesSinDuplicadosRule estudiantesSinDuplicadosRule = new EstudiantesSinDuplicadosRuleImpl();
    private final FichaPerfilExisteRule fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
    private final EstudiantesExistenRule estudiantesExistenRule = new EstudiantesExistenRuleImpl();
    private final EstudiantesNoVinculadosRule estudiantesNoVinculadosRule = new EstudiantesNoVinculadosRuleImpl();
    private final EstudianteFichaPerfilCupoDisponibleRule estudianteFichaPerfilCupoDisponibleRule = new EstudianteFichaPerfilCupoDisponibleRuleImpl();

    @Override
    public void validar(AgregacionEstudiantesFichaPerfilDomain entrada, boolean fichaExiste,
                        List<UUID> estudiantesExistentes, List<UUID> yaVinculados, long vinculadosActuales) {

        estudiantesSinDuplicadosRule.validar(entrada.getEstudiantes());

        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(entrada.getFichaPerfil(), fichaExiste));
        estudiantesExistenRule.validar(
                new ExistenciaEstudiantes(entrada.getEstudiantes(), estudiantesExistentes));
        estudiantesNoVinculadosRule.validar(new VinculosEstudiantesFicha(yaVinculados));

        estudianteFichaPerfilCupoDisponibleRule.validar(
                new CupoEstudiantesFicha(vinculadosActuales, entrada.getCantidad()));
    }
}
