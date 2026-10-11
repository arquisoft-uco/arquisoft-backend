package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionAsesorCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionCoordinadorCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionEstudianteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionRepresentanteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.secondaryport.ObservacionEvaluacionQueryOutputPort;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository.mapper.ObservacionEvaluacionAsesorQueryMapper;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository.mapper.ObservacionEvaluacionCoordinadorQueryMapper;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository.mapper.ObservacionEvaluacionEstudianteQueryMapper;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository.mapper.ObservacionEvaluacionRepresentanteQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ObservacionEvaluacionQueryOutputAdapter implements ObservacionEvaluacionQueryOutputPort {

    private final ObservacionEvaluacionEstudianteQueryRepository observacionEvaluacionEstudianteQueryRepository;
    private final ObservacionEvaluacionAsesorQueryRepository observacionEvaluacionAsesorQueryRepository;
    private final ObservacionEvaluacionRepresentanteQueryRepository observacionEvaluacionRepresentanteQueryRepository;
    private final ObservacionEvaluacionCoordinadorQueryRepository observacionEvaluacionCoordinadorQueryRepository;

    @Override
    public List<ObservacionEvaluacionReadModel> consultarPorEvaluacionYEstudiante(
            ObservacionEvaluacionEstudianteCriteria criteria) {
        return observacionEvaluacionEstudianteQueryRepository
                .findByEvaluacionFichaPerfilIdAndEstudianteIdOrderByObservacionAscIdAsc(
                        criteria.evaluacionFichaPerfil(), criteria.estudiante())
                .stream()
                .map(ObservacionEvaluacionEstudianteQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public List<ObservacionEvaluacionReadModel> consultarPorEvaluacionYAsesorFicha(
            ObservacionEvaluacionAsesorCriteria criteria) {
        return observacionEvaluacionAsesorQueryRepository
                .findByEvaluacionFichaPerfilIdAndAsesorFichaIdOrderByObservacionAscIdAsc(
                        criteria.evaluacionFichaPerfil(), criteria.asesorFicha())
                .stream()
                .map(ObservacionEvaluacionAsesorQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public List<ObservacionEvaluacionReadModel> consultarPorEvaluacionYRepresentanteComite(
            ObservacionEvaluacionRepresentanteCriteria criteria) {
        return observacionEvaluacionRepresentanteQueryRepository
                .findByEvaluacionFichaPerfilIdAndRepresentanteComiteIdOrderByObservacionAscIdAsc(
                        criteria.evaluacionFichaPerfil(), criteria.representanteComite())
                .stream()
                .map(ObservacionEvaluacionRepresentanteQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public List<ObservacionEvaluacionReadModel> consultarPorFicha(ObservacionEvaluacionCoordinadorCriteria criteria) {
        return observacionEvaluacionCoordinadorQueryRepository
                .findByFichaPerfilIdOrderByFechaEvaluacionAscEvaluacionFichaPerfilIdAscObservacionAscIdAsc(
                        criteria.fichaPerfil())
                .stream()
                .map(ObservacionEvaluacionCoordinadorQueryMapper::toReadModel)
                .toList();
    }
}
