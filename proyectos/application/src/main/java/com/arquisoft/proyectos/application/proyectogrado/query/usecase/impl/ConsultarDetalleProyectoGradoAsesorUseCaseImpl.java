package com.arquisoft.proyectos.application.proyectogrado.query.usecase.impl;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.AsesorAsignadoQueryFinder;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.finder.model.AsignacionAsesorConsultada;
import com.arquisoft.proyectos.application.proyectogrado.query.criteria.DetalleProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.finder.ProyectoGradoExisteQueryFinder;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.secondaryport.ProyectoGradoQueryOutputPort;
import com.arquisoft.proyectos.application.proyectogrado.query.usecase.ConsultarDetalleProyectoGradoAsesorUseCase;
import com.arquisoft.proyectos.application.proyectogrado.query.validator.ConsultarDetalleProyectoGradoAsesorValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarDetalleProyectoGradoAsesorUseCaseImpl implements ConsultarDetalleProyectoGradoAsesorUseCase {

    private final ProyectoGradoExisteQueryFinder proyectoGradoExisteQueryFinder;
    private final AsesorAsignadoQueryFinder asesorAsignadoQueryFinder;
    private final ConsultarDetalleProyectoGradoAsesorValidator validator;
    private final ProyectoGradoQueryOutputPort proyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public ProyectoGradoReadModel ejecutar(DetalleProyectoGradoAsesorCriteria criteria) {
        logger.debug(ProyectoGradoKey.LOG_CONSULTANDO_DETALLE_ASESOR, criteria.proyectoGrado(), criteria.asesor());

        var proyectoExiste = proyectoGradoExisteQueryFinder.obtener(criteria.proyectoGrado());
        var asesorAsignado = asesorAsignadoQueryFinder.obtener(
                new AsignacionAsesorConsultada(criteria.asesor(), criteria.proyectoGrado()));

        validator.validar(criteria, proyectoExiste, asesorAsignado);

        var resultado = proyectoGradoQueryOutputPort.consultarPorId(criteria.proyectoGrado());

        logger.debug(ProyectoGradoKey.LOG_CONSULTA_DETALLE_ASESOR_COMPLETADA, criteria.proyectoGrado());

        return resultado;
    }
}
