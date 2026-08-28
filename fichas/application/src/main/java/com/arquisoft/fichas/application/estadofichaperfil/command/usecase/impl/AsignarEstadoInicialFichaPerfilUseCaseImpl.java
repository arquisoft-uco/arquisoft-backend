package com.arquisoft.fichas.application.estadofichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AsignarEstadoInicialFichaPerfilUseCase;
import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AsignarEstadoInicialFichaPerfilValidator;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.AsignarEstudiantesFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.domain.fichaperfil.RegistroFichaPerfilDomain;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.mapper.EstadoFichaPerfilMapper;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsignarEstadoInicialFichaPerfilUseCaseImpl implements AsignarEstadoInicialFichaPerfilUseCase {

    private final EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;
    private final FichaPerfilExisteFinder fichaPerfilExisteFinder;
    private final AsignarEstadoInicialFichaPerfilValidator asignarEstadoInicialFichaPerfilValidator;
    private final AsignarEstudiantesFichaPerfilUseCase asignarEstudiantesFichaPerfilUseCase;
    private final AppLogger logger;

    @Override
    public void ejecutar(RegistroFichaPerfilDomain registro) {
        var estadoInicial = registro.getEstadoInicial();

        boolean fichaExiste = fichaPerfilExisteFinder.obtener(estadoInicial.getFichaPerfil());

        asignarEstadoInicialFichaPerfilValidator.validar(estadoInicial.getFichaPerfil(), fichaExiste);

        estadoFichaPerfilOutputPort.registrarEstadoInicial(EstadoFichaPerfilMapper.toEntity(estadoInicial));

        logger.debug(Mensajes.obtener(EstadoFichaPerfilKey.LOG_CREADO),
                estadoInicial.getId(),
                estadoInicial.getFichaPerfil(),
                estadoInicial.getEstadoFicha().getNombre());

        asignarEstudiantesFichaPerfilUseCase.ejecutar(registro.getEstudiantes());
    }
}
