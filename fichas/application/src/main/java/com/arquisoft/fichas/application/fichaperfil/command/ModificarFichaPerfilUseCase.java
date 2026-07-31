package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.validator.FichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaNoEncontradaException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarFichaPerfilUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final FichaPerfilValidator fichaPerfilValidator;
    private final AppLogger logger;

    public void ejecutar(ModificarFichaPerfilCommand command) {
        fichaPerfilValidator.validarEstudiantePropietario(
                new PropietarioFichaCriteria(command.fichaPerfil(), command.estudiante()));

        var ficha = fichaPerfilOutputPort.buscarPorId(command.fichaPerfil())
                .orElseThrow(() -> new FichaNoEncontradaException(command.fichaPerfil()));

        validarTituloDisponible(ficha, command.tituloProyecto());

        ficha.actualizarTitulo(command.tituloProyecto());
        fichaPerfilOutputPort.guardar(ficha);

        logger.info(FichasMessages.FichaPerfil.LOG_MODIFICADA, ficha.getId());
    }

    private void validarTituloDisponible(FichaPerfilAggregate ficha, String nuevoTitulo) {
        if (ficha.getTituloProyecto().equals(nuevoTitulo)) {
            return;
        }
        if (fichaPerfilOutputPort.existePorTituloProyecto(nuevoTitulo)) {
            throw new FichaTituloDuplicadoException(nuevoTitulo);
        }
    }
}
