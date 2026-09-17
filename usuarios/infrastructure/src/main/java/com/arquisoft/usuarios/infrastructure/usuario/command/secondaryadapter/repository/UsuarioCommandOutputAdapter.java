package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.mapper.UsuarioJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioCommandOutputAdapter implements UsuarioOutputPort {

    private final UsuarioCommandRepository usuarioCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(UsuarioEntity usuario) {
        usuarioCommandRepository.save(UsuarioJpaMapper.toJpaEntity(usuario));
        logger.debug(RegistrarUsuarioKey.LOG_USUARIO_GUARDADO, usuario.id());
    }

    @Override
    public Optional<UsuarioEntity> obtenerPorId(UUID id) {
        return usuarioCommandRepository.findById(id).map(UsuarioJpaMapper::toEntity);
    }

    @Override
    public boolean existePorIdentificador(String identificador) {
        return usuarioCommandRepository.existsByIdentificador(identificador);
    }

    @Override
    public boolean existePorEmail(String email) {
        return usuarioCommandRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existePorContacto(String contacto) {
        return usuarioCommandRepository.existsByContacto(contacto);
    }
}
