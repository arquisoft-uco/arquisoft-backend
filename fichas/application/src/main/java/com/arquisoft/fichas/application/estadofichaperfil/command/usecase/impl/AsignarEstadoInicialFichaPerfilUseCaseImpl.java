package com.arquisoft.fichas.application.estadofichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AsignarEstadoInicialFichaPerfilUseCase;
import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AsignarEstadoInicialFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsignarEstadoInicialFichaPerfilUseCaseImpl implements AsignarEstadoInicialFichaPerfilUseCase {

    private final EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;
    private final AsignarEstadoInicialFichaPerfilValidator asignarEstadoInicialFichaPerfilValidator;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public void ejecutar(EstadoFichaPerfilDomain estadoInicial) {
        asignarEstadoInicialFichaPerfilValidator.validar(estadoInicial.getFichaPerfil());

        estadoFichaPerfilOutputPort.guardar(estadoInicial);

        logger.info(catalog.obtener(FichasKeys.EstadoFichaPerfil.LOG_CREADO),
                estadoInicial.getId(),
                estadoInicial.getFichaPerfil(),
                estadoInicial.getEstadoFicha().getNombre());
    }
}
