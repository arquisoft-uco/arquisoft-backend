package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.validator.RemoverEstudianteFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.RemocionEstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.VinculoEstudianteFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteFichaPerfilValidatorImpl implements RemoverEstudianteFichaPerfilValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final EstudiantesExistenRule estudiantesExistenRule;
    private final VinculoEstudianteFichaExisteRule vinculoEstudianteFichaExisteRule;

    @Override
    public void validar(RemocionEstudianteFichaPerfilDomain entrada) {
        fichaPerfilExisteRule.validar(entrada.getFichaPerfil());
        estudiantesExistenRule.validar(List.of(entrada.getEstudiante()));
        vinculoEstudianteFichaExisteRule.validar(entrada);
    }
}
