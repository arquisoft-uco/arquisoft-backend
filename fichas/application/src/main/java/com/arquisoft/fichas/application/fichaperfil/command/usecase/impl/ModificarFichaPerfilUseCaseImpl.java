package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.ModificarFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.ModificarFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarFichaPerfilUseCaseImpl implements ModificarFichaPerfilUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final ModificarFichaPerfilValidator modificarFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificarFichaPerfilCommand entrada) {
        modificarFichaPerfilValidator.validarPropiedad(entrada.fichaPerfil(), entrada.estudiante());

        var ficha = fichaPerfilOutputPort.buscarPorId(entrada.fichaPerfil())
                .orElseThrow(() -> new FichaNoEncontradaException(entrada.fichaPerfil()));

        modificarFichaPerfilValidator.validarTitulo(ficha, entrada.tituloProyecto());

        ficha.actualizarTitulo(entrada.tituloProyecto());
        fichaPerfilOutputPort.guardar(ficha);

        logger.info(FichasMessages.FichaPerfil.LOG_MODIFICADA, ficha.getId());
    }
}
