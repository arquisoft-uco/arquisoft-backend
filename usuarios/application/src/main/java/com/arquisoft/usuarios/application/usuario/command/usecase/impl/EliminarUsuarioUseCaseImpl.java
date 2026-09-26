package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.EliminarUsuarioKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.asesor.command.finder.AsesorPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesorficha.command.finder.AsesorFichaPorUsuarioFinder;
import com.arquisoft.usuarios.application.coordinador.command.finder.CoordinadorPorUsuarioFinder;
import com.arquisoft.usuarios.application.estudiante.command.finder.EstudiantePorUsuarioFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.usecase.CambiarEstadoUsuarioUseCase;
import com.arquisoft.usuarios.application.usuario.command.usecase.EliminarUsuarioUseCase;
import com.arquisoft.usuarios.application.usuario.command.validator.EliminarUsuarioValidator;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.CambioEstadoUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.EliminacionUsuarioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarUsuarioUseCaseImpl implements EliminarUsuarioUseCase {

    private final UsuarioOutputPort usuarioOutputPort;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final EstudiantePorUsuarioFinder estudiantePorUsuarioFinder;
    private final AsesorPorUsuarioFinder asesorPorUsuarioFinder;
    private final AsesorFichaPorUsuarioFinder asesorFichaPorUsuarioFinder;
    private final CoordinadorPorUsuarioFinder coordinadorPorUsuarioFinder;
    private final EliminarUsuarioValidator eliminarUsuarioValidator;
    private final CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionUsuarioDomain eliminacion) {
        logger.info(EliminarUsuarioKey.LOG_ELIMINANDO, eliminacion.getUsuario());

        var usuario = usuarioPorIdFinder.obtener(eliminacion.getUsuario());
        var estudiante = estudiantePorUsuarioFinder.obtener(eliminacion.getUsuario());
        var asesor = asesorPorUsuarioFinder.obtener(eliminacion.getUsuario());
        var asesorFicha = asesorFichaPorUsuarioFinder.obtener(eliminacion.getUsuario());
        var coordinador = coordinadorPorUsuarioFinder.obtener(eliminacion.getUsuario());

        logger.debug(EliminarUsuarioKey.LOG_VERIFICACION_ELIMINAR, eliminacion.getUsuario(),
                !usuario.esVacio(), usuario.estaEliminado(), usuario.estaActivo(), !estudiante.esVacio(),
                !asesor.esVacio(), !asesorFicha.esVacio(), !coordinador.esVacio());

        eliminarUsuarioValidator.validar(eliminacion.getUsuario(), usuario, estudiante, asesor, asesorFicha,
                coordinador);

        usuario.eliminar(UtilFecha.generarInstanteActual());
        usuarioOutputPort.eliminarLogica(usuario.getId(), usuario.getEliminadoEn());

        if (usuario.estaActivo()) {
            cambiarEstadoUsuarioUseCase.ejecutar(
                    CambioEstadoUsuarioDomain.crear(usuario.getId(), EstadoUsuario.INACTIVO.getId()));
        }

        logger.info(EliminarUsuarioKey.LOG_ELIMINADO, eliminacion.getUsuario(), usuario.estaActivo());
    }
}
