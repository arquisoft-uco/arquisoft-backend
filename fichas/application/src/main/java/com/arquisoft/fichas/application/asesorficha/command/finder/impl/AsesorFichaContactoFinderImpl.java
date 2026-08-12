package com.arquisoft.fichas.application.asesorficha.command.finder.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaContactoFinder;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesorFicha;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaContactoFinderImpl implements AsesorFichaContactoFinder {

    private final AsesorFichaOutputPort asesorFichaOutputPort;

    @Override
    public Optional<ContactoAsesorFicha> obtener(UUID asesorFicha) {
        return asesorFichaOutputPort.buscarContactoPorId(asesorFicha);
    }
}
