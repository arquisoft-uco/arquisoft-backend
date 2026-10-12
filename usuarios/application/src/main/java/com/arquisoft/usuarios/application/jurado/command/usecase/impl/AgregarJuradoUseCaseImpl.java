package com.arquisoft.usuarios.application.jurado.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarJuradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.usuarios.application.jurado.command.finder.JuradoPorUsuarioFinder;
import com.arquisoft.usuarios.application.jurado.command.secondaryport.JuradoOutputPort;
import com.arquisoft.usuarios.application.jurado.command.secondaryport.mapper.JuradoMapper;
import com.arquisoft.usuarios.application.jurado.command.usecase.AgregarJuradoUseCase;
import com.arquisoft.usuarios.application.jurado.command.validator.AgregarJuradoValidator;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
import com.arquisoft.usuarios.domain.jurado.event.JuradoAgregadoEvent;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarJuradoUseCaseImpl implements AgregarJuradoUseCase {

    private final JuradoOutputPort juradoOutputPort;
    private final JuradoPorUsuarioFinder juradoPorUsuarioFinder;
    private final AgregarJuradoValidator agregarJuradoValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(UsuarioDomain usuario) {
        var jurado = juradoPorUsuarioFinder.obtener(usuario.getId());
        logger.debug(AgregarJuradoKey.LOG_VERIFICACION_AGREGAR,
                usuario.getId(), !jurado.esVacio(), jurado.estaEliminado());

        agregarJuradoValidator.validar(usuario.getId(), jurado);

        if (jurado.esVacio()) {
            juradoOutputPort.guardar(
                    JuradoMapper.toEntity(JuradoDomain.crear(usuario.getId())));
        } else {
            reactivar(jurado);
        }

        eventPublisher.publish(new JuradoAgregadoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));
    }

    private void reactivar(JuradoDomain jurado) {
        jurado.reactivar();
        juradoOutputPort.reactivar(jurado.getUsuario());
        logger.info(AgregarJuradoKey.LOG_REACTIVADO, jurado.getUsuario());
    }
}
