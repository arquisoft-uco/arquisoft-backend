package com.arquisoft.fichas.application.estadofichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AsignarEstadoInicialFichaPerfilUseCase;
import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AsignarEstadoInicialFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsignarEstadoInicialFichaPerfilUseCaseImpl implements AsignarEstadoInicialFichaPerfilUseCase {

    private final EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;
    private final AsignarEstadoInicialFichaPerfilValidator asignarEstadoInicialFichaPerfilValidator;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public void ejecutar(EstadoFichaPerfilDomain estadoInicial) {
        asignarEstadoInicialFichaPerfilValidator.validar(estadoInicial.getFichaPerfil());

        estadoFichaPerfilOutputPort.registrarEstadoInicial(estadoInicial);

        logger.info(catalogo.obtener(EstadoFichaPerfilKey.LOG_CREADO),
                estadoInicial.getId(),
                estadoInicial.getFichaPerfil(),
                estadoInicial.getEstadoFicha().getNombre());
    }
}
