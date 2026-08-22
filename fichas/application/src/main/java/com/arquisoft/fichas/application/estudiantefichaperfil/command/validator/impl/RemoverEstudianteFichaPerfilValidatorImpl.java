package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.RemoverEstudianteFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiante.model.ExistenciaEstudiantes;
import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiante.rules.impl.EstudiantesExistenRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.RemocionEstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ExistenciaVinculoEstudianteFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.VinculoEstudianteFichaExisteRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.VinculoEstudianteFichaExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RemoverEstudianteFichaPerfilValidatorImpl implements RemoverEstudianteFichaPerfilValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final EstudiantesExistenRule estudiantesExistenRule;
    private final VinculoEstudianteFichaExisteRule vinculoEstudianteFichaExisteRule;

    // Las Rules no son beans y no necesitan serlo: son funciones puras, sin estado ni
    // dependencias, asi que no hay variabilidad ni ciclo de vida que un contenedor deba
    // gestionar. Construirlas aqui deja en un unico sitio que reglas ejecuta este
    // validator, y elimina el bean por regla que habia que recordar en cada regla nueva.
    public RemoverEstudianteFichaPerfilValidatorImpl() {
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
        this.estudiantesExistenRule = new EstudiantesExistenRuleImpl();
        this.vinculoEstudianteFichaExisteRule = new VinculoEstudianteFichaExisteRuleImpl();
    }

    @Override
    public void validar(RemocionEstudianteFichaPerfilDomain entrada, boolean fichaExiste,
                        List<UUID> estudiantesExistentes, boolean vinculoExiste) {

        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(entrada.getFichaPerfil(), fichaExiste));

        estudiantesExistenRule.validar(
                new ExistenciaEstudiantes(List.of(entrada.getEstudiante()), estudiantesExistentes));

        vinculoEstudianteFichaExisteRule.validar(new ExistenciaVinculoEstudianteFicha(
                entrada.getFichaPerfil(), entrada.getEstudiante(), vinculoExiste));
    }
}
