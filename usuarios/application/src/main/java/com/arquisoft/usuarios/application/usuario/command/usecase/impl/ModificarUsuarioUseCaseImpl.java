package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.message.key.usuarios.ModificarUsuarioKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.asesor.command.usecase.AgregarAsesorUseCase;
import com.arquisoft.usuarios.application.asesorficha.command.usecase.AgregarAsesorFichaUseCase;
import com.arquisoft.usuarios.application.coordinador.command.usecase.AgregarCoordinadorUseCase;
import com.arquisoft.usuarios.application.estudiante.command.usecase.AgregarEstudianteUseCase;
import com.arquisoft.usuarios.application.usuario.command.finder.ContactoOtroUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.EmailOtraIdentidadExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.EmailOtroUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.IdentificadorOtroUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.model.UnicidadOtroUsuario;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.ModificacionIdentidadEntity;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.mapper.UsuarioMapper;
import com.arquisoft.usuarios.application.usuario.command.usecase.ModificarUsuarioUseCase;
import com.arquisoft.usuarios.application.usuario.command.validator.ModificarUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.ModificacionUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.event.UsuarioModificadoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarUsuarioUseCaseImpl implements ModificarUsuarioUseCase {

    private final UsuarioOutputPort usuarioOutputPort;
    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    private final UsuarioPorIdFinder usuarioPorIdFinder;
    private final IdentificadorOtroUsuarioExisteFinder identificadorOtroUsuarioExisteFinder;
    private final EmailOtroUsuarioExisteFinder emailOtroUsuarioExisteFinder;
    private final EmailOtraIdentidadExisteFinder emailOtraIdentidadExisteFinder;
    private final ContactoOtroUsuarioExisteFinder contactoOtroUsuarioExisteFinder;
    private final ModificarUsuarioValidator modificarUsuarioValidator;
    private final AgregarEstudianteUseCase agregarEstudianteUseCase;
    private final AgregarCoordinadorUseCase agregarCoordinadorUseCase;
    private final AgregarAsesorFichaUseCase agregarAsesorFichaUseCase;
    private final AgregarAsesorUseCase agregarAsesorUseCase;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionUsuarioDomain modificacion) {
        logger.info(ModificarUsuarioKey.LOG_MODIFICANDO, modificacion.getUsuario());

        var usuario = usuarioPorIdFinder.obtener(modificacion.getUsuario());
        var identificadorDuplicado = UtilObjeto.noEsNulo(modificacion.getIdentificador())
                && identificadorOtroUsuarioExisteFinder.obtener(
                        new UnicidadOtroUsuario(modificacion.getUsuario(), modificacion.getIdentificador()));
        var unicidadEmail = new UnicidadOtroUsuario(modificacion.getUsuario(), modificacion.getEmail());
        var emailDuplicado = UtilObjeto.noEsNulo(modificacion.getEmail())
                && (emailOtroUsuarioExisteFinder.obtener(unicidadEmail)
                || emailOtraIdentidadExisteFinder.obtener(unicidadEmail));
        var contactoDuplicado = UtilObjeto.noEsNulo(modificacion.getContacto())
                && contactoOtroUsuarioExisteFinder.obtener(
                        new UnicidadOtroUsuario(modificacion.getUsuario(), modificacion.getContacto()));

        logger.debug(ModificarUsuarioKey.LOG_VERIFICACION_MODIFICAR, modificacion.getUsuario(),
                !usuario.esVacio(), usuario.estaActivo(), identificadorDuplicado, emailDuplicado,
                contactoDuplicado);

        modificarUsuarioValidator.validar(modificacion, usuario, identificadorDuplicado, emailDuplicado,
                contactoDuplicado);

        usuario.modificar(modificacion);
        usuarioOutputPort.actualizar(UsuarioMapper.toEntity(usuario));

        if (modificacion.modificaDatos()) {
            eventPublisher.publish(new UsuarioModificadoEvent(usuario.getId(), usuario.getIdentificador(),
                    usuario.getNombre(), usuario.getEmail()));
        }

        if (modificacion.contieneRol(UsuariosRealmRoles.ESTUDIANTE)) {
            agregarEstudianteUseCase.ejecutar(usuario);
        }

        if (modificacion.contieneRol(UsuariosRealmRoles.COORDINADOR)) {
            agregarCoordinadorUseCase.ejecutar(usuario);
        }

        if (modificacion.contieneRol(UsuariosRealmRoles.ASESOR_FICHA)) {
            agregarAsesorFichaUseCase.ejecutar(usuario);
        }

        if (modificacion.contieneRol(UsuariosRealmRoles.ASESOR)) {
            agregarAsesorUseCase.ejecutar(usuario);
        }

        if (modificacion.modificaIdentidad()) {
            proveedorIdentidadOutputPort.actualizar(new ModificacionIdentidadEntity(
                    modificacion.getUsuario(), modificacion.getEmail(), modificacion.getNombres(),
                    modificacion.getApellidos()));
        }

        proveedorIdentidadOutputPort.asignarRealmRoles(modificacion.getUsuario(), modificacion.getRoles());

        logger.info(ModificarUsuarioKey.LOG_MODIFICADO, modificacion.getUsuario(),
                modificacion.getRoles().size());
    }
}
