package com.arquisoft.fichas.application.revisionitem.command.finder.impl;

import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.finder.AsesorFichaPropietarioFinder;
import com.arquisoft.fichas.domain.revisionitem.AgregacionRevisionItemDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsesorFichaPropietarioFinderImpl implements AsesorFichaPropietarioFinder {

    private final FichaPerfilDelItemFinder fichaPerfilDelItemFinder;
    private final FichaPerfilFinder fichaPerfilFinder;

    @Override
    public Boolean obtener(AgregacionRevisionItemDomain agregacion) {
        return fichaPerfilDelItemFinder.obtener(agregacion.getItem())
                .flatMap(fichaPerfilFinder::obtener)
                .map(ficha -> ficha.getAsesorFicha().equals(agregacion.getAsesorFicha()))
                .orElse(false);
    }
}
