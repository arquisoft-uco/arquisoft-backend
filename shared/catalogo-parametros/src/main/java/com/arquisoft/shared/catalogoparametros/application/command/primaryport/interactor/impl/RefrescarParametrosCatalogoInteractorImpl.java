package com.arquisoft.shared.catalogoparametros.application.command.primaryport.interactor.impl;

import com.arquisoft.shared.catalogoparametros.CatalogoParametrosEncendido;
import com.arquisoft.shared.catalogoparametros.application.command.primaryport.interactor.RefrescarParametrosCatalogoInteractor;
import com.arquisoft.shared.catalogoparametros.application.command.result.RefrescoParametrosCatalogoResult;
import com.arquisoft.shared.catalogoparametros.application.command.usecase.RefrescarParametrosCatalogoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@CatalogoParametrosEncendido
@RequiredArgsConstructor
public class RefrescarParametrosCatalogoInteractorImpl implements RefrescarParametrosCatalogoInteractor {

    private final RefrescarParametrosCatalogoUseCase refrescarParametrosCatalogoUseCase;

    @Override
    public RefrescoParametrosCatalogoResult ejecutar() {
        return refrescarParametrosCatalogoUseCase.ejecutar();
    }
}
