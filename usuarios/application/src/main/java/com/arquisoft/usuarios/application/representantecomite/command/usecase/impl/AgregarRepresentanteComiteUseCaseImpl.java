package com.arquisoft.usuarios.application.representantecomite.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarRepresentanteComiteKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.usuarios.application.representantecomite.command.finder.RepresentanteComitePorUsuarioFinder;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.mapper.RepresentanteComiteMapper;
import com.arquisoft.usuarios.application.representantecomite.command.usecase.AgregarRepresentanteComiteUseCase;
import com.arquisoft.usuarios.application.representantecomite.command.validator.AgregarRepresentanteComiteValidator;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.representantecomite.event.RepresentanteComiteAgregadoEvent;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarRepresentanteComiteUseCaseImpl implements AgregarRepresentanteComiteUseCase {

    private final RepresentanteComiteOutputPort representanteComiteOutputPort;
    private final RepresentanteComitePorUsuarioFinder representanteComitePorUsuarioFinder;
    private final AgregarRepresentanteComiteValidator agregarRepresentanteComiteValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(UsuarioDomain usuario) {
        var representanteComite = representanteComitePorUsuarioFinder.obtener(usuario.getId());
        logger.debug(AgregarRepresentanteComiteKey.LOG_VERIFICACION_AGREGAR,
                usuario.getId(), !representanteComite.esVacio(), representanteComite.estaEliminado());

        agregarRepresentanteComiteValidator.validar(usuario.getId(), representanteComite);

        if (representanteComite.esVacio()) {
            representanteComiteOutputPort.guardar(
                    RepresentanteComiteMapper.toEntity(RepresentanteComiteDomain.crear(usuario.getId())));
        } else {
            reactivar(representanteComite);
        }

        eventPublisher.publish(new RepresentanteComiteAgregadoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));
    }

    private void reactivar(RepresentanteComiteDomain representanteComite) {
        representanteComite.reactivar();
        representanteComiteOutputPort.reactivar(representanteComite.getUsuario());
        logger.info(AgregarRepresentanteComiteKey.LOG_REACTIVADO, representanteComite.getUsuario());
    }
}
