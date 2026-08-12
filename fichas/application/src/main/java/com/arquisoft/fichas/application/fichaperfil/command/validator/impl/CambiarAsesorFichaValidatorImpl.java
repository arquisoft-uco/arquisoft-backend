package com.arquisoft.fichas.application.fichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.fichaperfil.command.validator.CambiarAsesorFichaValidator;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.AsesorFichaComparacion;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaDiferenteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CambiarAsesorFichaValidatorImpl implements CambiarAsesorFichaValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final AsesorFichaExisteRule asesorFichaExisteRule;
    private final EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;
    private final AsesorFichaDiferenteRule asesorFichaDiferenteRule;

    @Override
    public void validar(CambioAsesorFichaDomain cambio, Optional<FichaPerfilDomain> ficha, boolean asesorExiste,
                        Optional<EstadoFicha> estadoActual) {

        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(cambio.getFichaPerfil(), ficha.isPresent()));
        asesorFichaExisteRule.validar(new ExistenciaAsesorFicha(cambio.getNuevoAsesorFicha(), asesorExiste));

        estadoFichaPerfilEnTerminalRule.validar(new EstadoActualFicha(cambio.getFichaPerfil(), estadoActual));

        ficha.map(FichaPerfilDomain::getAsesorFicha)
                .ifPresent(asesorActual -> asesorFichaDiferenteRule.validar(
                        new AsesorFichaComparacion(cambio.getNuevoAsesorFicha(), asesorActual)));
    }
}
