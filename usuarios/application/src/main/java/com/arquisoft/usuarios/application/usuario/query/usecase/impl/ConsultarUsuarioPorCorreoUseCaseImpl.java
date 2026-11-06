package com.arquisoft.usuarios.application.usuario.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.ConsultarUsuarioPorCorreoKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioPorCorreoCriteria;
import com.arquisoft.usuarios.application.usuario.query.finder.UsuarioPorCorreoQueryFinder;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioPorCorreoReadModel;
import com.arquisoft.usuarios.application.usuario.query.usecase.ConsultarUsuarioPorCorreoUseCase;
import com.arquisoft.usuarios.application.usuario.query.validator.ConsultarUsuarioPorCorreoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarUsuarioPorCorreoUseCaseImpl implements ConsultarUsuarioPorCorreoUseCase {

    private final UsuarioPorCorreoQueryFinder usuarioPorCorreoQueryFinder;
    private final ConsultarUsuarioPorCorreoValidator consultarUsuarioPorCorreoValidator;
    private final AppLogger logger;

    @Override
    public UsuarioPorCorreoReadModel ejecutar(UsuarioPorCorreoCriteria criteria) {
        logger.debug(ConsultarUsuarioPorCorreoKey.LOG_CONSULTANDO, UtilTexto.enmascararCorreo(criteria.correo()));

        var encontrado = usuarioPorCorreoQueryFinder.obtener(criteria.correo());

        consultarUsuarioPorCorreoValidator.validar(encontrado);

        logger.debug(ConsultarUsuarioPorCorreoKey.LOG_CONSULTA_COMPLETADA);

        return new UsuarioPorCorreoReadModel(encontrado.getId());
    }
}
