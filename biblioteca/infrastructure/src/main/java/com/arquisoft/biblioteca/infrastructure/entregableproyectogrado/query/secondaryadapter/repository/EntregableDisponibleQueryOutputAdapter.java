package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.criteria.EntregableDisponibleCriteria;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel.EntregableDisponibleReadModel;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.secondaryport.EntregableDisponibleQueryOutputPort;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.secondaryadapter.repository.mapper.EntregableDisponibleQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EntregableDisponibleQueryOutputAdapter implements EntregableDisponibleQueryOutputPort {

    private static final String RUTA_DESEMPATE = "id";

    private final EntregableDisponibleQueryRepository entregableDisponibleQueryRepository;
    private final AutorEntregableDisponibleQueryRepository autorEntregableDisponibleQueryRepository;
    private final EntregableDisponibleJpaSpecification specification;

    @Override
    public PaginatedResult<EntregableDisponibleReadModel> consultar(EntregableDisponibleCriteria criteria) {
        var pageable = PageableMapper.toPageable(
                criteria, EntregableDisponibleSortMapper::traducir, RUTA_DESEMPATE);
        var pagina = entregableDisponibleQueryRepository.findAll(specification.desdeCriteria(criteria), pageable);

        var autoresPorProyecto = pagina.isEmpty()
                ? Map.<UUID, List<AutorEntregableDisponibleJpaQueryEntity>>of()
                : autoresPorProyecto(pagina.getContent());

        return PaginationMapper.toResult(pagina.map(entregable -> EntregableDisponibleQueryMapper.toReadModel(
                entregable, autoresPorProyecto.getOrDefault(entregable.getProyectoGradoId(), List.of()))));
    }

    private Map<UUID, List<AutorEntregableDisponibleJpaQueryEntity>> autoresPorProyecto(
            List<EntregableDisponibleJpaQueryEntity> entregables) {
        var proyectos = entregables.stream()
                .map(EntregableDisponibleJpaQueryEntity::getProyectoGradoId)
                .distinct()
                .toList();

        return autorEntregableDisponibleQueryRepository
                .findByProyectoGradoIdInOrderByNombreAscAutorIdAsc(proyectos).stream()
                .collect(Collectors.groupingBy(AutorEntregableDisponibleJpaQueryEntity::getProyectoGradoId));
    }
}
