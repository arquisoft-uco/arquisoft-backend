package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.mapper.UsuarioJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioCommandOutputAdapter implements UsuarioOutputPort {

    private final UsuarioCommandRepository usuarioCommandRepository;

    @Override
    public void guardar(UsuarioEntity usuario) {
        usuarioCommandRepository.save(UsuarioJpaMapper.toJpaEntity(usuario));
    }

    @Override
    public boolean existePorEmail(String email) {
        return usuarioCommandRepository.existsByEmail(email);
    }
}
