package com.arquisoft.usuarios.application.usuario.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarUsuariosAdministradorKey;
import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.UsuarioQueryOutputPort;
import com.arquisoft.usuarios.application.usuario.query.usecase.ConsultarUsuariosAdministradorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarUsuariosAdministradorUseCaseImpl implements ConsultarUsuariosAdministradorUseCase {

    private final UsuarioQueryOutputPort usuarioQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<UsuarioReadModel> ejecutar(UsuarioCriteria entrada) {
        logger.debug(ConsultarUsuariosAdministradorKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = usuarioQueryOutputPort.consultarTodos(entrada);

        logger.debug(ConsultarUsuariosAdministradorKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
