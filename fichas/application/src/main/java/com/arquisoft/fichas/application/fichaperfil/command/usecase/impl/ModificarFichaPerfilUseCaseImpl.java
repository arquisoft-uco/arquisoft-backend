package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.ModificarFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.ModificarFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarFichaPerfilUseCaseImpl implements ModificarFichaPerfilUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final FichaPerfilFinder fichaPerfilFinder;
    private final ModificarFichaPerfilValidator modificarFichaPerfilValidator;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public void ejecutar(ModificarFichaPerfilCommand entrada) {
        modificarFichaPerfilValidator.validarPropiedad(entrada.fichaPerfil(), entrada.estudiante());

        var ficha = fichaPerfilFinder.obtener(entrada.fichaPerfil());

        modificarFichaPerfilValidator.validarTitulo(ficha, entrada.tituloProyecto());

        ficha.actualizarTitulo(entrada.tituloProyecto());
        fichaPerfilOutputPort.guardar(ficha);

        logger.info(catalog.obtener(FichasKeys.FichaPerfil.LOG_MODIFICADA), ficha.getId());
    }
}
