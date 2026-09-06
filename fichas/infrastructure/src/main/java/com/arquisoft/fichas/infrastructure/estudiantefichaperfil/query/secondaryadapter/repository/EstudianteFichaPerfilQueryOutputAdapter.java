package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.secondaryport.EstudianteFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.secondaryadapter.repository.mapper.EstudianteFichaPerfilQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteFichaPerfilQueryOutputAdapter implements EstudianteFichaPerfilQueryOutputPort {

    private final EstudianteFichaPerfilQueryRepository estudianteFichaPerfilQueryRepository;

    @Override
    public List<EstudianteFichaPerfilReadModel> consultarPorFicha(UUID fichaPerfil) {
        return estudianteFichaPerfilQueryRepository
                .findByFichaPerfilIdOrderByNombreAsc(fichaPerfil)
                .stream()
                .map(EstudianteFichaPerfilQueryMapper::toReadModel)
                .toList();
    }
}
