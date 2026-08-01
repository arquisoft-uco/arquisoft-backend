package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.ModificarFichaPerfilUseCase;
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
public class ModificarFichaPerfilUseCaseImpl implements ModificarFichaPerfilUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final FichaPerfilValidator fichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificarFichaPerfilCommand entrada) {
        fichaPerfilValidator.validarEstudiantePropietario(
                new PropietarioFichaCriteria(entrada.fichaPerfil(), entrada.estudiante()));

        var ficha = fichaPerfilOutputPort.buscarPorId(entrada.fichaPerfil())
                .orElseThrow(() -> new FichaNoEncontradaException(entrada.fichaPerfil()));

        validarTituloDisponible(ficha, entrada.tituloProyecto());

        ficha.actualizarTitulo(entrada.tituloProyecto());
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
