package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.usuarios.application.coordinador.command.usecase.AgregarCoordinadorUseCase;
import com.arquisoft.usuarios.application.estudiante.command.usecase.AgregarEstudianteUseCase;
import com.arquisoft.usuarios.application.usuario.command.finder.ContactoUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.EmailIdentidadExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.EmailUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.IdentificadorUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.RegistroIdentidadEntity;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.mapper.UsuarioMapper;
import com.arquisoft.usuarios.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import com.arquisoft.usuarios.application.usuario.command.validator.RegistrarUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.RegistroUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioUseCase {

    private final UsuarioOutputPort usuarioOutputPort;
    private final ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    private final IdentificadorUsuarioExisteFinder identificadorUsuarioExisteFinder;
    private final EmailUsuarioExisteFinder emailUsuarioExisteFinder;
    private final EmailIdentidadExisteFinder emailIdentidadExisteFinder;
    private final ContactoUsuarioExisteFinder contactoUsuarioExisteFinder;
    private final RegistrarUsuarioValidator registrarUsuarioValidator;
    private final AgregarEstudianteUseCase agregarEstudianteUseCase;
    private final AgregarCoordinadorUseCase agregarCoordinadorUseCase;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(RegistroUsuarioDomain registro) {
        logger.info(RegistrarUsuarioKey.LOG_REGISTRANDO);

        var identificadorYaExiste = identificadorUsuarioExisteFinder.obtener(registro.getIdentificador());
        var emailYaExiste = emailUsuarioExisteFinder.obtener(registro.getEmail())
                || emailIdentidadExisteFinder.obtener(registro.getEmail());
        var contactoYaExiste = contactoUsuarioExisteFinder.obtener(registro.getContacto());

        logger.debug(RegistrarUsuarioKey.LOG_VERIFICACION_REGISTRAR,
                identificadorYaExiste, emailYaExiste, contactoYaExiste);

        registrarUsuarioValidator.validar(registro, identificadorYaExiste, emailYaExiste, contactoYaExiste);

        var identidadId = proveedorIdentidadOutputPort.registrar(new RegistroIdentidadEntity(
                registro.getEmail(), registro.getNombres(), registro.getApellidos(), registro.getRoles()));

        var usuario = UsuarioDomain.crear(identidadId, registro);
        usuarioOutputPort.guardar(UsuarioMapper.toEntity(usuario));

        if (registro.getRoles().contains(UsuariosRealmRoles.ESTUDIANTE)) {
            agregarEstudianteUseCase.ejecutar(usuario);
        }

        if (registro.getRoles().contains(UsuariosRealmRoles.COORDINADOR)) {
            agregarCoordinadorUseCase.ejecutar(usuario);
        }

        logger.info(RegistrarUsuarioKey.LOG_REGISTRADO,
                identidadId, UtilTexto.enmascararCorreo(registro.getEmail()));

        return identidadId;
    }
}
