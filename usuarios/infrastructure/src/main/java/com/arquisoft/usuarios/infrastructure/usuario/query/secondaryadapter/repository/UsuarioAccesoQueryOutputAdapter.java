package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.usuario.query.secondaryport.UsuarioAccesoQueryOutputPort;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.entity.UsuarioAccesoEntity;
import com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository.mapper.UsuarioAccesoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioAccesoQueryOutputAdapter implements UsuarioAccesoQueryOutputPort {

    private final UsuarioAccesoQueryRepository usuarioAccesoQueryRepository;

    @Override
    public Optional<UsuarioAccesoEntity> obtenerPorId(UUID usuario) {
        return usuarioAccesoQueryRepository.findById(usuario).map(UsuarioAccesoQueryMapper::toEntity);
    }
}
