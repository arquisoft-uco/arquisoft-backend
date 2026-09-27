package com.arquisoft.usuarios.application.estadousuario.query.usecase.impl;

import com.arquisoft.usuarios.application.estadousuario.query.readmodel.EstadoUsuarioReadModel;
import com.arquisoft.usuarios.application.estadousuario.query.secondaryport.EstadoUsuarioQueryOutputPort;
import com.arquisoft.usuarios.application.estadousuario.query.usecase.ConsultarEstadosUsuarioUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.ConsultarEstadosUsuarioKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosUsuarioUseCaseImpl implements ConsultarEstadosUsuarioUseCase {

    private final EstadoUsuarioQueryOutputPort estadoUsuarioQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EstadoUsuarioReadModel> ejecutar() {
        var resultado = estadoUsuarioQueryOutputPort.consultarTodos();

        logger.debug(ConsultarEstadosUsuarioKey.LOG_CONSULTA_COMPLETADA, resultado.size());

        return resultado;
    }
}
