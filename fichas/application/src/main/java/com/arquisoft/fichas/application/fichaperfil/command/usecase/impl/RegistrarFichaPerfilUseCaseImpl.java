package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.RegistrarFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.RegistrarFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilUseCaseImpl implements RegistrarFichaPerfilUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final RegistrarFichaPerfilValidator registrarFichaPerfilValidator;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public UUID ejecutar(FichaPerfilDomain ficha) {
        registrarFichaPerfilValidator.validar(ficha);

        fichaPerfilOutputPort.registrarFicha(ficha);

        logger.info(catalogo.obtener(FichaPerfilKey.LOG_REGISTRADA), ficha.getId());
        return ficha.getId();
    }
}
