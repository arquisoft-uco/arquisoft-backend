package com.arquisoft.entregables.application.paqueteentregable.query.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.EntregableProyectoGradoAccesoQueryFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.EstudianteParticipaEntregableQueryFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.model.EstudianteEntregable;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.finder.EstadoActualEntregableProyectoGradoQueryFinder;
import com.arquisoft.entregables.application.paqueteentregable.query.criteria.DescargaPaqueteEntregableCriteria;
import com.arquisoft.entregables.application.paqueteentregable.query.finder.PaqueteEntregableQueryFinder;
import com.arquisoft.entregables.application.paqueteentregable.query.readmodel.EnlaceDescargaPaqueteEntregableReadModel;
import com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.EnlaceDescargaPaqueteQueryOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.query.usecase.DescargarPaqueteEntregableUseCase;
import com.arquisoft.entregables.application.paqueteentregable.query.validator.DescargarPaqueteEntregableValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DescargarPaqueteEntregableUseCaseImpl implements DescargarPaqueteEntregableUseCase {

    private final EntregableProyectoGradoAccesoQueryFinder entregableProyectoGradoAccesoQueryFinder;
    private final EstadoActualEntregableProyectoGradoQueryFinder estadoActualEntregableProyectoGradoQueryFinder;
    private final EstudianteParticipaEntregableQueryFinder estudianteParticipaEntregableQueryFinder;
    private final PaqueteEntregableQueryFinder paqueteEntregableQueryFinder;
    private final DescargarPaqueteEntregableValidator descargarPaqueteEntregableValidator;
    private final EnlaceDescargaPaqueteQueryOutputPort enlaceDescargaPaqueteQueryOutputPort;
    private final AppLogger logger;

    @Override
    public EnlaceDescargaPaqueteEntregableReadModel ejecutar(DescargaPaqueteEntregableCriteria entrada) {
        logger.debug(PaqueteEntregableKey.LOG_DESCARGANDO, entrada.entregableProyectoGrado(), entrada.actor());

        var entregable = entregableProyectoGradoAccesoQueryFinder.obtener(entrada.entregableProyectoGrado());
        var estadoActual = estadoActualEntregableProyectoGradoQueryFinder.obtener(entrada.entregableProyectoGrado());
        var participa = estudianteParticipaEntregableQueryFinder.obtener(
                new EstudianteEntregable(entrada.actor(), entrada.entregableProyectoGrado()));
        var paquete = paqueteEntregableQueryFinder.obtener(entrada.entregableProyectoGrado());

        logger.debug(PaqueteEntregableKey.LOG_VERIFICACION_DESCARGA,
                !entregable.esVacio(), !estadoActual.esVacio(), participa, !paquete.esVacio());

        descargarPaqueteEntregableValidator.validar(entrada, entregable, estadoActual, participa, paquete);

        var enlace = enlaceDescargaPaqueteQueryOutputPort.generar(
                paquete.getBucket(), paquete.getClaveObjeto(), paquete.getNombreArchivo());

        logger.debug(PaqueteEntregableKey.LOG_DESCARGA_GENERADA, entrada.entregableProyectoGrado(), enlace.expiraEn());
        return enlace;
    }
}
