package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.model.CambioAsesorFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaDiferenteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CambiarAsesorFichaValidator {

    private final AsesorFichaExisteRule asesorFichaExisteRule;
    private final EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;
    private final AsesorFichaDiferenteRule asesorFichaDiferenteRule;

    public void validar(FichaPerfilAggregate ficha, UUID asesorFichaActual) {
        asesorFichaExisteRule.validar(ficha.getAsesorFicha());
        estadoFichaPerfilEnTerminalRule.validar(ficha.getId());
        asesorFichaDiferenteRule.validar(new CambioAsesorFichaCriteria(ficha.getAsesorFicha(), asesorFichaActual));
    }
}
