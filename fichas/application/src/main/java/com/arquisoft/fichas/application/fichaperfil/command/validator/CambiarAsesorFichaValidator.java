package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CambiarAsesorFichaValidator {

    private final AsesorFichaExisteRule asesorFichaExisteRule;

    public void validar(UUID nuevoAsesorFicha) {
        asesorFichaExisteRule.validar(nuevoAsesorFicha);
    }
}
