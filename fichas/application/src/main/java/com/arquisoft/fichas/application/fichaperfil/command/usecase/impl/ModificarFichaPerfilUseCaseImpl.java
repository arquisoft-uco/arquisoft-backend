package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.ModificarFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.ModificarFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.ModificarFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarFichaPerfilUseCaseImpl implements ModificarFichaPerfilUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final ModificarFichaPerfilValidator modificarFichaPerfilValidator;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public void ejecutar(ModificarFichaPerfilDomain entrada) {
        modificarFichaPerfilValidator.validar(entrada);

        fichaPerfilOutputPort.actualizarTitulo(entrada.getFichaPerfil(), entrada.getTituloProyecto());

        logger.info(catalogo.obtener(FichaPerfilKey.LOG_MODIFICADA), entrada.getFichaPerfil());
    }
}
