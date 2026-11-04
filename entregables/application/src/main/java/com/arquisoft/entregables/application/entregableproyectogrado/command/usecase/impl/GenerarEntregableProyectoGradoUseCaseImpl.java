package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.asesor.command.finder.AsesorVigenteFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.UltimaVersionEntregableProyectoGradoFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.mapper.EntregableProyectoGradoMapper;
import com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.GenerarEntregableProyectoGradoUseCase;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.GenerarEntregableProyectoGradoValidator;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.entregables.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GenerarEntregableProyectoGradoUseCaseImpl implements GenerarEntregableProyectoGradoUseCase {

    private final EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;
    private final ProyectoGradoExisteFinder proyectoGradoExisteFinder;
    private final AsesorVigenteFinder asesorVigenteFinder;
    private final UltimaVersionEntregableProyectoGradoFinder ultimaVersionEntregableProyectoGradoFinder;
    private final GenerarEntregableProyectoGradoValidator generarEntregableProyectoGradoValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(GeneracionEntregableProyectoGradoDomain entrada) {
        logger.info(EntregableProyectoGradoKey.LOG_GENERANDO, entrada.getProyectoGrado(), entrada.getAsesor());

        var proyectoExiste = proyectoGradoExisteFinder.obtener(entrada.getProyectoGrado());
        var asesorVigente = asesorVigenteFinder.obtener(entrada.getAsesor());
        var ultimaVersion = ultimaVersionEntregableProyectoGradoFinder.obtener(entrada.getProyectoGrado());

        logger.debug(EntregableProyectoGradoKey.LOG_VERIFICACION_GENERAR,
                proyectoExiste, asesorVigente, ultimaVersion);

        generarEntregableProyectoGradoValidator.validar(
                new ExistenciaProyectoGrado(entrada.getProyectoGrado(), proyectoExiste),
                new VigenciaAsesor(entrada.getAsesor(), asesorVigente));

        var entregable = EntregableProyectoGradoDomain.crear(
                entrada.getProyectoGrado(), ultimaVersion, entrada.getAsesor());
        entregableProyectoGradoOutputPort.registrar(EntregableProyectoGradoMapper.toEntity(entregable));

        logger.info(EntregableProyectoGradoKey.LOG_GENERADO,
                entregable.getId(), entregable.getProyectoGrado(), entregable.getVersion());

        return entregable.getId();
    }
}
