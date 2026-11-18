package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.usuario.query.criteria.PerfilUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.PerfilUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.PerfilUsuarioQueryOutputPort;
import com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.repository.mapper.PerfilUsuarioQueryMapper;
import com.arquisoft.usuarios.infrastructure.usuario.exception.PerfilUsuarioNoDisponibleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PerfilUsuarioQueryOutputAdapter implements PerfilUsuarioQueryOutputPort {

    private final PerfilUsuarioQueryRepository perfilUsuarioQueryRepository;

    @Override
    public PerfilUsuarioReadModel consultar(PerfilUsuarioCriteria criteria) {
        return perfilUsuarioQueryRepository.findById(criteria.usuario())
                .map(PerfilUsuarioQueryMapper::toReadModel)
                .orElseThrow(() -> new PerfilUsuarioNoDisponibleException(criteria.usuario()));
    }
}
