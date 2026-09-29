package com.arquisoft.usuarios.application.asesorficha.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.message.key.usuarios.RemoverAsesorFichaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.asesorficha.command.finder.AsesorFichaPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.usuarios.application.asesorficha.command.usecase.RemoverAsesorFichaUseCase;
import com.arquisoft.usuarios.application.asesorficha.command.validator.RemoverAsesorFichaValidator;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.asesorficha.event.AsesorFichaRemovidoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverAsesorFichaUseCaseImpl implements RemoverAsesorFichaUseCase {

    private final AsesorFichaOutputPort asesorFichaOutputPort;
    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    private final AsesorFichaPorUsuarioFinder asesorFichaPorUsuarioFinder;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final RemoverAsesorFichaValidator removerAsesorFichaValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(AsesorFichaDomain entrada) {
        logger.info(RemoverAsesorFichaKey.LOG_REMOVIENDO, entrada.getUsuario());

        var asesorFicha = asesorFichaPorUsuarioFinder.obtener(entrada.getUsuario());
        var usuario = usuarioPorIdFinder.obtener(entrada.getUsuario());
        logger.debug(RemoverAsesorFichaKey.LOG_VERIFICACION_REMOVER,
                entrada.getUsuario(), !asesorFicha.esVacio(), asesorFicha.estaEliminado());

        removerAsesorFichaValidator.validar(entrada.getUsuario(), asesorFicha);

        asesorFicha.remover(UtilFecha.generarInstanteActual());
        asesorFichaOutputPort.eliminarLogica(asesorFicha.getUsuario(), asesorFicha.getEliminadoEn());
        proveedorIdentidadOutputPort.revocarRealmRole(asesorFicha.getUsuario(), UsuariosRealmRoles.ASESOR_FICHA);

        eventPublisher.publish(new AsesorFichaRemovidoEvent(
                usuario.getId(), usuario.getIdentificador(), usuario.getNombre(), usuario.getEmail()));

        logger.info(RemoverAsesorFichaKey.LOG_REMOVIDO, entrada.getUsuario());
    }
}
