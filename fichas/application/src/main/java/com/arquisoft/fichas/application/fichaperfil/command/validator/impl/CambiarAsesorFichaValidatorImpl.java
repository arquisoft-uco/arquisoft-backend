package com.arquisoft.fichas.application.fichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.fichaperfil.command.validator.CambiarAsesorFichaValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.CambiarAsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.CambioAsesorFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaDiferenteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CambiarAsesorFichaValidatorImpl implements CambiarAsesorFichaValidator {

    private final AsesorFichaExisteRule asesorFichaExisteRule;
    private final EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;
    private final AsesorFichaDiferenteRule asesorFichaDiferenteRule;

    @Override
    public void validar(CambiarAsesorFichaDomain cambio, UUID asesorFichaActual) {
        asesorFichaExisteRule.validar(cambio.getNuevoAsesorFicha());
        estadoFichaPerfilEnTerminalRule.validar(cambio.getFichaPerfil());
        asesorFichaDiferenteRule.validar(
                new CambioAsesorFichaCriteria(cambio.getNuevoAsesorFicha(), asesorFichaActual));
    }
}
