package com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.RegistrarFichaPerfilInteractor;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.mapper.RegistrarFichaPerfilMapper;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.RegistrarFichaPerfilUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilInteractorImpl implements RegistrarFichaPerfilInteractor {

    private final RegistrarFichaPerfilUseCase registrarFichaPerfilUseCase;
    private final AppLogger logger;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(RegistrarFichaPerfilCommand command) {
        var registro = RegistrarFichaPerfilMapper.toDomain(command);

        var fichaPerfil = registrarFichaPerfilUseCase.ejecutar(registro);

        logger.info(Mensajes.obtener(FichaPerfilKey.LOG_REGISTRO_COMPLETADO),
                fichaPerfil, registro.getEstudiantes().getCantidad());

        return fichaPerfil;
    }
}
