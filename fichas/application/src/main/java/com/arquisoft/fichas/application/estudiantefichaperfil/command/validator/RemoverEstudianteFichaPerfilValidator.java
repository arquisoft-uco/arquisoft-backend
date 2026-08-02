package com.arquisoft.fichas.application.estudiantefichaperfil.command.validator;

import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculacionEstudianteCriteria;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.VinculoEstudianteFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteFichaPerfilValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final EstudiantesExistenRule estudiantesExistenRule;
    private final VinculoEstudianteFichaExisteRule vinculoEstudianteFichaExisteRule;

    public void validar(UUID fichaPerfil, UUID estudiante) {
        fichaPerfilExisteRule.validar(fichaPerfil);
        estudiantesExistenRule.validar(List.of(estudiante));
        vinculoEstudianteFichaExisteRule.validar(new VinculacionEstudianteCriteria(fichaPerfil, estudiante));
    }
}
