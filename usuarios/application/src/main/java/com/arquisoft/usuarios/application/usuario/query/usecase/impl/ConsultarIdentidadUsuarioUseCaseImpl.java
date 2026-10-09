package com.arquisoft.usuarios.application.usuario.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.ConsultarIdentidadUsuarioKey;
import com.arquisoft.usuarios.application.usuario.query.criteria.IdentidadUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.finder.UsuarioPorIdQueryFinder;
import com.arquisoft.usuarios.application.usuario.query.readmodel.IdentidadUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.IdentidadUsuarioQueryOutputPort;
import com.arquisoft.usuarios.application.usuario.query.usecase.ConsultarIdentidadUsuarioUseCase;
import com.arquisoft.usuarios.application.usuario.query.validator.ConsultarIdentidadUsuarioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarIdentidadUsuarioUseCaseImpl implements ConsultarIdentidadUsuarioUseCase {

    private final UsuarioPorIdQueryFinder usuarioPorIdQueryFinder;
    private final ConsultarIdentidadUsuarioValidator consultarIdentidadUsuarioValidator;
    private final IdentidadUsuarioQueryOutputPort identidadUsuarioQueryOutputPort;
    private final AppLogger logger;

    @Override
    public IdentidadUsuarioReadModel ejecutar(IdentidadUsuarioCriteria criteria) {
        logger.debug(ConsultarIdentidadUsuarioKey.LOG_CONSULTANDO, criteria.usuario());

        var encontrado = usuarioPorIdQueryFinder.obtener(criteria.usuario());

        consultarIdentidadUsuarioValidator.validar(criteria.usuario(), encontrado);

        var identidad = identidadUsuarioQueryOutputPort.consultar(criteria);

        logger.debug(ConsultarIdentidadUsuarioKey.LOG_CONSULTA_COMPLETADA, criteria.usuario());

        return identidad;
    }
}
