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

    private final EstudiantesSinDuplicadosRule estudiantesSinDuplicadosRule;
    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final EstudiantesExistenRule estudiantesExistenRule;
    private final EstudiantesNoVinculadosRule estudiantesNoVinculadosRule;
    private final EstudianteFichaPerfilCupoDisponibleRule estudianteFichaPerfilCupoDisponibleRule;

    // Las Rules no son beans y no necesitan serlo: son funciones puras, sin estado ni
    // dependencias, asi que no hay variabilidad ni ciclo de vida que un contenedor deba
    // gestionar. Construirlas aqui deja en un unico sitio que reglas ejecuta este
    // validator, y elimina el bean por regla que habia que recordar en cada regla nueva.
    public AsignarEstudiantesFichaPerfilValidatorImpl() {
        this.estudiantesSinDuplicadosRule = new EstudiantesSinDuplicadosRuleImpl();
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
        this.estudiantesExistenRule = new EstudiantesExistenRuleImpl();
        this.estudiantesNoVinculadosRule = new EstudiantesNoVinculadosRuleImpl();
        this.estudianteFichaPerfilCupoDisponibleRule = new EstudianteFichaPerfilCupoDisponibleRuleImpl();
    }

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
