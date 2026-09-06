package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.secondaryport.EstudianteFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilEstudianteQueryOutputPort;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.mapper.FichaPerfilEstudianteQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FichaPerfilEstudianteQueryOutputAdapter implements FichaPerfilEstudianteQueryOutputPort {

    private final FichaPerfilEstudianteQueryRepository fichaPerfilEstudianteQueryRepository;
    private final EstudianteFichaPerfilQueryOutputPort estudianteFichaPerfilQueryOutputPort;

    @Override
    public Optional<FichaPerfilEstudianteReadModel> consultar(FichaPerfilEstudianteCriteria criteria) {
        var estudiantes = estudianteFichaPerfilQueryOutputPort.consultarPorFicha(criteria.fichaPerfil());

        if (!estaVinculado(estudiantes, criteria.estudiante())) {
            return Optional.empty();
        }

        return fichaPerfilEstudianteQueryRepository.findById(criteria.fichaPerfil())
                .map(cabecera -> FichaPerfilEstudianteQueryMapper.toReadModel(cabecera, estudiantes));
    }

    private static boolean estaVinculado(List<EstudianteFichaPerfilReadModel> estudiantes, UUID estudiante) {
        return estudiantes.stream()
                .anyMatch(vinculado -> vinculado.estudianteId().equals(estudiante));
    }
}
