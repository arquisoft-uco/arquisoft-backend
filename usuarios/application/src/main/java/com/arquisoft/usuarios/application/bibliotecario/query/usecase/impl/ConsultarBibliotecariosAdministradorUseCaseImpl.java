package com.arquisoft.usuarios.application.bibliotecario.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarBibliotecariosAdministradorKey;
import com.arquisoft.usuarios.application.bibliotecario.query.criteria.BibliotecarioCriteria;
import com.arquisoft.usuarios.application.bibliotecario.query.readmodel.BibliotecarioReadModel;
import com.arquisoft.usuarios.application.bibliotecario.query.secondaryport.BibliotecarioQueryOutputPort;
import com.arquisoft.usuarios.application.bibliotecario.query.usecase.ConsultarBibliotecariosAdministradorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarBibliotecariosAdministradorUseCaseImpl implements ConsultarBibliotecariosAdministradorUseCase {

    private final BibliotecarioQueryOutputPort bibliotecarioQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<BibliotecarioReadModel> ejecutar(BibliotecarioCriteria entrada) {
        logger.debug(ConsultarBibliotecariosAdministradorKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = bibliotecarioQueryOutputPort.consultarTodos(entrada);

        logger.debug(ConsultarBibliotecariosAdministradorKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
