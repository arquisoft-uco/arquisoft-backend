package com.arquisoft.usuarios.application.jurado.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.message.key.usuarios.RemoverJuradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.jurado.command.finder.JuradoPorUsuarioFinder;
import com.arquisoft.usuarios.application.jurado.command.secondaryport.JuradoOutputPort;
import com.arquisoft.usuarios.application.jurado.command.usecase.RemoverJuradoUseCase;
import com.arquisoft.usuarios.application.jurado.command.validator.RemoverJuradoValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
import com.arquisoft.usuarios.domain.jurado.event.JuradoRemovidoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverJuradoUseCaseImpl implements RemoverJuradoUseCase {

    private final JuradoOutputPort juradoOutputPort;
    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    private final JuradoPorUsuarioFinder juradoPorUsuarioFinder;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final RemoverJuradoValidator removerJuradoValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(JuradoDomain entrada) {
        logger.info(RemoverJuradoKey.LOG_REMOVIENDO, entrada.getUsuario());

        var jurado = juradoPorUsuarioFinder.obtener(entrada.getUsuario());
        var usuario = usuarioPorIdFinder.obtener(entrada.getUsuario());
        logger.debug(RemoverJuradoKey.LOG_VERIFICACION_REMOVER,
                entrada.getUsuario(), !usuario.esVacio(), !jurado.esVacio(), jurado.estaEliminado());

        removerJuradoValidator.validar(entrada.getUsuario(), usuario, jurado);

        jurado.remover(UtilFecha.generarInstanteActual());
        juradoOutputPort.eliminarLogica(jurado.getUsuario(), jurado.getEliminadoEn());
        proveedorIdentidadOutputPort.revocarRol(jurado.getUsuario(), UsuariosRoles.JURADO);

        eventPublisher.publish(new JuradoRemovidoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));

        logger.info(RemoverJuradoKey.LOG_REMOVIDO, entrada.getUsuario());
    }
}
