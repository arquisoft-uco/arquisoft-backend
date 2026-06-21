package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.asesorficha.query.port.out.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.RegistrarFichaPerfilInputPort;
import com.arquisoft.fichas.application.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrarFichaPerfilUseCase implements RegistrarFichaPerfilInputPort {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(RegistrarFichaPerfilCommand command) {
        // POL-03: validar que el asesor ficha exista (lookup vía query side de la vista materializada)
        if (!asesorFichaQueryOutputPort.existsById(command.asesorFichaId())) {
            throw new AsesorFichaNoEncontradoException(command.asesorFichaId());
        }

        // POL-02: validar que el título sea único
        if (fichaPerfilOutputPort.existsByTituloProyecto(command.tituloProyecto())) {
            throw new FichaTituloDuplicadoException(command.tituloProyecto());
        }

        FichaPerfilAggregate ficha = FichaPerfilAggregate.crear(
                command.tituloProyecto(),
                command.asesorFichaId()
        );

        fichaPerfilOutputPort.guardar(ficha);

        log.info(FichasMessages.FichaPerfil.LOG_REGISTRADA, ficha.getId());
        return ficha.getId();
    }
}
