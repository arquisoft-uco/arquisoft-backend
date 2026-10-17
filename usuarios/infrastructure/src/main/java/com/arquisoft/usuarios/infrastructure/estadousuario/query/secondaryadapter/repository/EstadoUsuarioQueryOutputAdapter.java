package com.arquisoft.usuarios.infrastructure.estadousuario.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.estadousuario.query.readmodel.EstadoUsuarioReadModel;
import com.arquisoft.usuarios.application.estadousuario.query.secondaryport.EstadoUsuarioQueryOutputPort;
import com.arquisoft.usuarios.infrastructure.estadousuario.query.secondaryadapter.repository.mapper.EstadoUsuarioQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstadoUsuarioQueryOutputAdapter implements EstadoUsuarioQueryOutputPort {

    private final EstadoUsuarioQueryRepository repository;

    @Override
    public List<EstadoUsuarioReadModel> consultarTodos() {
        return repository.findAll()
                .stream()
                .map(EstadoUsuarioQueryMapper::toReadModel)
                .toList();
    }
}
