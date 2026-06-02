package com.arquisoft.fichas.application.fichaperfil.command;

import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.RegistrarFichaPerfilInputPort;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
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
    @Transactional
    public UUID ejecutar(RegistrarFichaPerfilCommand command) {
        FichaPerfilAggregate ficha = FichaPerfilAggregate.build(
                command.tituloProyecto(),
                command.asesorFichaId()
        );

        fichaPerfilOutputPort.guardar(ficha);

        log.info("Ficha de perfil registrada — id={}", ficha.getId());
        return ficha.getId();
    }
}
