package com.arquisoft.artefactos.application.paqueteentregable.command.usecase.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.finder.ArtefactosParaPaqueteFinder;
import com.arquisoft.artefactos.application.paqueteentregable.command.finder.PaqueteEntregableExisteFinder;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.GeneracionPaqueteEntregableResult;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.mapper.GeneracionPaqueteEntregableResultMapper;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.AlmacenamientoPaqueteOutputPort;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.mapper.PaqueteEntregableMapper;
import com.arquisoft.artefactos.application.paqueteentregable.command.usecase.GenerarPaqueteEntregableUseCase;
import com.arquisoft.artefactos.application.paqueteentregable.command.validator.GenerarPaqueteEntregableValidator;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.domain.paqueteentregable.GeneracionPaqueteEntregableDomain;
import com.arquisoft.artefactos.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.artefactos.domain.paqueteentregable.event.PaqueteEntregableGeneradoEvent;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArchivoPaquete;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.PaqueteEntregableKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerarPaqueteEntregableUseCaseImpl implements GenerarPaqueteEntregableUseCase {

    private final PaqueteEntregableOutputPort paqueteEntregableOutputPort;
    private final AlmacenamientoPaqueteOutputPort almacenamientoPaqueteOutputPort;
    private final PaqueteEntregableExisteFinder paqueteEntregableExisteFinder;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final ArtefactosParaPaqueteFinder artefactosParaPaqueteFinder;
    private final GenerarPaqueteEntregableValidator generarPaqueteEntregableValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public GeneracionPaqueteEntregableResult ejecutar(GeneracionPaqueteEntregableDomain generacion) {
        var yaExiste = paqueteEntregableExisteFinder.obtener(generacion.getEntregableProyectoGrado());
        logger.debug(PaqueteEntregableKey.LOG_VERIFICACION_PREVIA, generacion.getEntregableProyectoGrado(), yaExiste);

        if (yaExiste) {
            return GeneracionPaqueteEntregableResultMapper.toResultDuplicado(generacion);
        }

        var proyecto = proyectoGradoPorIdFinder.obtener(generacion.getProyectoGrado());
        var artefactos = artefactosParaPaqueteFinder.obtener(generacion.getProyectoGrado());

        logger.debug(PaqueteEntregableKey.LOG_VERIFICACION_GENERAR,
                !proyecto.esVacio(), artefactos.incluidos().size(), artefactos.excluidos().size());

        generarPaqueteEntregableValidator.validar(
                new ExistenciaProyectoGrado(generacion.getProyectoGrado(), !proyecto.esVacio()));

        var archivo = almacenamientoPaqueteOutputPort.armar(
                PaqueteEntregableMapper.toContenido(generacion, proyecto, artefactos));

        var paquete = PaqueteEntregableDomain.crear(
                generacion, archivo.claveObjeto(), archivo.tamanoBytes());
        paqueteEntregableOutputPort.registrar(PaqueteEntregableMapper.toEntity(paquete));

        eventPublisher.publish(PaqueteEntregableGeneradoEvent.de(
                paquete.getId(), generacion.getEntregableProyectoGrado(),
                new ArchivoPaquete(
                        archivo.bucket(), archivo.claveObjeto(), archivo.nombreArchivo(), archivo.tamanoBytes()),
                paquete.getGeneradoEn(),
                PaqueteEntregableMapper.toArtefactosGenerados(artefactos.incluidos(), archivo.entradas())));

        return GeneracionPaqueteEntregableResultMapper.toResultGenerado(paquete);
    }
}
