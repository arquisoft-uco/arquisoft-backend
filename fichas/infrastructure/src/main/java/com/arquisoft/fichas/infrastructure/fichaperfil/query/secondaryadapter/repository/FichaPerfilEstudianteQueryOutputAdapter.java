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
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FichaPerfilEstudianteQueryOutputAdapter implements FichaPerfilEstudianteQueryOutputPort {

    private final FichaPerfilEstudianteQueryRepository fichaPerfilEstudianteQueryRepository;
    private final EstudianteFichaPerfilQueryOutputPort estudianteFichaPerfilQueryOutputPort;

    @Override
    public List<FichaPerfilEstudianteReadModel> consultarPorEstudiante(FichaPerfilEstudianteCriteria criteria) {
        var vinculos = estudianteFichaPerfilQueryOutputPort
                .consultarVigentesDeFichasDelEstudiante(criteria.estudiante());

        if (vinculos.isEmpty()) {
            return List.of();
        }

        var estudiantesPorFicha = vinculos.stream()
                .collect(Collectors.groupingBy(EstudianteFichaPerfilReadModel::fichaPerfilId));

        return fichaPerfilEstudianteQueryRepository
                .findByIdInOrderByTituloProyectoAsc(estudiantesPorFicha.keySet())
                .stream()
                .map(cabecera -> FichaPerfilEstudianteQueryMapper
                        .toReadModel(cabecera, estudiantesPorFicha.get(cabecera.getId())))
                .toList();
    }
}
