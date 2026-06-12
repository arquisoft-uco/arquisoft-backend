package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.RegistrarFichaPerfilInputPort;
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

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(RegistrarFichaPerfilCommand command) {
        FichaPerfilAggregate ficha = FichaPerfilAggregate.crear(
                command.tituloProyecto(),
                command.asesorFichaId()
        );

        fichaPerfilOutputPort.guardar(ficha);

        log.info(FichasMessages.FichaPerfil.LOG_REGISTRADA, ficha.getId());
        return ficha.getId();
    }
}
