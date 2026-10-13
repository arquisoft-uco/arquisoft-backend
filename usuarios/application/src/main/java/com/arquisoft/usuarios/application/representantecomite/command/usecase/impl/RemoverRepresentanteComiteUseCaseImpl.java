package com.arquisoft.usuarios.application.representantecomite.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.message.key.usuarios.RemoverRepresentanteComiteKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.representantecomite.command.finder.RepresentanteComitePorUsuarioFinder;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.usuarios.application.representantecomite.command.usecase.RemoverRepresentanteComiteUseCase;
import com.arquisoft.usuarios.application.representantecomite.command.validator.RemoverRepresentanteComiteValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.representantecomite.event.RepresentanteComiteRemovidoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverRepresentanteComiteUseCaseImpl implements RemoverRepresentanteComiteUseCase {

    private final RepresentanteComiteOutputPort representanteComiteOutputPort;
    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    private final RepresentanteComitePorUsuarioFinder representanteComitePorUsuarioFinder;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final RemoverRepresentanteComiteValidator removerRepresentanteComiteValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(RepresentanteComiteDomain entrada) {
        logger.info(RemoverRepresentanteComiteKey.LOG_REMOVIENDO, entrada.getUsuario());

        var representanteComite = representanteComitePorUsuarioFinder.obtener(entrada.getUsuario());
        var usuario = usuarioPorIdFinder.obtener(entrada.getUsuario());
        logger.debug(RemoverRepresentanteComiteKey.LOG_VERIFICACION_REMOVER,
                entrada.getUsuario(), !representanteComite.esVacio(), representanteComite.estaEliminado());

        removerRepresentanteComiteValidator.validar(entrada.getUsuario(), representanteComite);

        representanteComite.remover(UtilFecha.generarInstanteActual());
        representanteComiteOutputPort.eliminarLogica(representanteComite.getUsuario(), representanteComite.getEliminadoEn());
        proveedorIdentidadOutputPort.revocarRealmRole(representanteComite.getUsuario(), UsuariosRealmRoles.REPRESENTANTE_COMITE);

        eventPublisher.publish(new RepresentanteComiteRemovidoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));

        logger.info(RemoverRepresentanteComiteKey.LOG_REMOVIDO, entrada.getUsuario());
    }
}
