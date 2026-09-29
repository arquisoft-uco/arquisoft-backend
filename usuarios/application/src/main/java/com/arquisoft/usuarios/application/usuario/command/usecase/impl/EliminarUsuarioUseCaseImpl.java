package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.EliminarUsuarioKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.administrador.command.finder.AdministradorPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesor.command.finder.AsesorPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesorficha.command.finder.AsesorFichaPorUsuarioFinder;
import com.arquisoft.usuarios.application.coordinador.command.finder.CoordinadorPorUsuarioFinder;
import com.arquisoft.usuarios.application.estudiante.command.finder.EstudiantePorUsuarioFinder;
import com.arquisoft.usuarios.application.representantecomite.command.finder.RepresentanteComitePorUsuarioFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.usecase.CambiarEstadoUsuarioUseCase;
import com.arquisoft.usuarios.application.usuario.command.usecase.EliminarUsuarioUseCase;
import com.arquisoft.usuarios.application.usuario.command.validator.EliminarUsuarioValidator;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.CambioEstadoUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.EliminacionUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.RolesUsuario;
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
    private final RepresentanteComitePorUsuarioFinder representanteComitePorUsuarioFinder;
    private final AdministradorPorUsuarioFinder administradorPorUsuarioFinder;
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
        var representanteComite = representanteComitePorUsuarioFinder.obtener(eliminacion.getUsuario());
        var administrador = administradorPorUsuarioFinder.obtener(eliminacion.getUsuario());
        // TODO HU251 (jurado), HU241 (bibliotecario):
        //  obtener aqui el rol con su {Rol}PorUsuarioFinder y agregarlo a RolesUsuario, para que
        //  UsuarioSinRolesVigentesRule impida eliminar al usuario mientras ese rol no este removido.

        logger.debug(EliminarUsuarioKey.LOG_VERIFICACION_ELIMINAR, eliminacion.getUsuario(),
                !usuario.esVacio(), usuario.estaEliminado(), usuario.estaActivo(), !estudiante.esVacio(),
                !asesor.esVacio(), !asesorFicha.esVacio(), !coordinador.esVacio(), !representanteComite.esVacio(),
                !administrador.esVacio());

        eliminarUsuarioValidator.validar(eliminacion.getUsuario(), usuario, new RolesUsuario(
                eliminacion.getUsuario(), estudiante, asesor, asesorFicha, coordinador, representanteComite,
                administrador));

        usuario.eliminar(UtilFecha.generarInstanteActual());
        usuarioOutputPort.eliminarLogica(usuario.getId(), usuario.getEliminadoEn());

        if (usuario.estaActivo()) {
            cambiarEstadoUsuarioUseCase.ejecutar(
                    CambioEstadoUsuarioDomain.crear(usuario.getId(), EstadoUsuario.INACTIVO.getId()));
        }

        logger.info(EliminarUsuarioKey.LOG_ELIMINADO, eliminacion.getUsuario(), usuario.estaActivo());
    }
}
