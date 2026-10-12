package com.arquisoft.usuarios.application.asesor.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.message.key.usuarios.RemoverAsesorKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.asesor.command.finder.AsesorPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.usuarios.application.asesor.command.usecase.RemoverAsesorUseCase;
import com.arquisoft.usuarios.application.asesor.command.validator.RemoverAsesorValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesor.event.AsesorRemovidoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverAsesorUseCaseImpl implements RemoverAsesorUseCase {

    private final AsesorOutputPort asesorOutputPort;
    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    private final AsesorPorUsuarioFinder asesorPorUsuarioFinder;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final RemoverAsesorValidator removerAsesorValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(AsesorDomain entrada) {
        logger.info(RemoverAsesorKey.LOG_REMOVIENDO, entrada.getUsuario());

        var asesor = asesorPorUsuarioFinder.obtener(entrada.getUsuario());
        var usuario = usuarioPorIdFinder.obtener(entrada.getUsuario());
        logger.debug(RemoverAsesorKey.LOG_VERIFICACION_REMOVER,
                entrada.getUsuario(), !asesor.esVacio(), asesor.estaEliminado());

        removerAsesorValidator.validar(entrada.getUsuario(), asesor);

        asesor.remover(UtilFecha.generarInstanteActual());
        asesorOutputPort.eliminarLogica(asesor.getUsuario(), asesor.getEliminadoEn());
        proveedorIdentidadOutputPort.revocarRealmRole(asesor.getUsuario(), UsuariosRealmRoles.ASESOR);

        eventPublisher.publish(new AsesorRemovidoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));

        logger.info(RemoverAsesorKey.LOG_REMOVIDO, entrada.getUsuario());
    }
}
