package com.arquisoft.usuarios.application.usuario.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.ConsultarMiPerfilKey;
import com.arquisoft.usuarios.application.usuario.query.criteria.PerfilUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.finder.UsuarioPorIdQueryFinder;
import com.arquisoft.usuarios.application.usuario.query.readmodel.PerfilUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.PerfilUsuarioQueryOutputPort;
import com.arquisoft.usuarios.application.usuario.query.usecase.ConsultarMiPerfilUseCase;
import com.arquisoft.usuarios.application.usuario.query.validator.ConsultarMiPerfilValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarMiPerfilUseCaseImpl implements ConsultarMiPerfilUseCase {

    private final UsuarioPorIdQueryFinder usuarioPorIdQueryFinder;
    private final ConsultarMiPerfilValidator consultarMiPerfilValidator;
    private final PerfilUsuarioQueryOutputPort perfilUsuarioQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PerfilUsuarioReadModel ejecutar(PerfilUsuarioCriteria criteria) {
        logger.debug(ConsultarMiPerfilKey.LOG_CONSULTANDO, criteria.usuario());

        var encontrado = usuarioPorIdQueryFinder.obtener(criteria.usuario());

        consultarMiPerfilValidator.validar(criteria.usuario(), encontrado);

        var perfil = perfilUsuarioQueryOutputPort.consultar(criteria);

        logger.debug(ConsultarMiPerfilKey.LOG_CONSULTA_COMPLETADA, criteria.usuario(), perfil.roles().size());

        return perfil;
    }
}
