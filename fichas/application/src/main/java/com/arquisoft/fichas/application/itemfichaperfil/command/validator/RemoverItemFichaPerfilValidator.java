package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.rules.EstudiantePropietarioFichaRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemoverItemFichaPerfilValidator {

    private final EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;

    public void validar(UUID fichaPerfil, UUID estudiante) {
        estudiantePropietarioFichaRule.validar(new PropietarioFichaCriteria(fichaPerfil, estudiante));
    }
}
