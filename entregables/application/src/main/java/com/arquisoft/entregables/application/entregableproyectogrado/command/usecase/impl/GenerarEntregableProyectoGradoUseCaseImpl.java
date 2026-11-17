package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ArtefactosVigentesProyectoFinder;
import com.arquisoft.entregables.application.artefactoentregable.command.primaryport.mapper.AgregarArtefactosEntregableMapper;
import com.arquisoft.entregables.application.artefactoentregable.command.usecase.AgregarArtefactosEntregableUseCase;
import com.arquisoft.entregables.application.asesor.command.finder.AsesorVigenteFinder;
import com.arquisoft.entregables.application.asesorproyectogrado.command.finder.AsesorVigenteProyectoGradoFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.EntregableVigenteExisteFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.finder.UltimaVersionEntregableProyectoGradoFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EjecutorTransaccionalOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.mapper.EntregableProyectoGradoMapper;
import com.arquisoft.entregables.application.entregableproyectogrado.command.usecase.GenerarEntregableProyectoGradoUseCase;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.ConfirmarGeneracionEntregableProyectoGradoValidator;
import com.arquisoft.entregables.application.entregableproyectogrado.command.validator.GenerarEntregableProyectoGradoValidator;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.EstadoEntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.mapper.EstadoEntregableProyectoGradoMapper;
import com.arquisoft.entregables.application.paqueteentregable.command.finder.PaquetesProyectoGradoFinder;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.ArmadoPaqueteOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.mapper.PaqueteEntregableMapper;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.model.ArchivoPaqueteArmado;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.event.EntregableProyectoGradoGeneradoEvent;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenerarEntregableProyectoGradoUseCaseImpl implements GenerarEntregableProyectoGradoUseCase {

    private final EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;
    private final EstadoEntregableProyectoGradoOutputPort estadoEntregableProyectoGradoOutputPort;
    private final PaqueteEntregableOutputPort paqueteEntregableOutputPort;
    private final ArmadoPaqueteOutputPort armadoPaqueteOutputPort;
    private final EjecutorTransaccionalOutputPort ejecutorTransaccionalOutputPort;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final ProyectoGradoBloqueadoPorIdFinder proyectoGradoBloqueadoPorIdFinder;
    private final AsesorVigenteFinder asesorVigenteFinder;
    private final AsesorVigenteProyectoGradoFinder asesorVigenteProyectoGradoFinder;
    private final EntregableVigenteExisteFinder entregableVigenteExisteFinder;
    private final UltimaVersionEntregableProyectoGradoFinder ultimaVersionEntregableProyectoGradoFinder;
    private final ArtefactosVigentesProyectoFinder artefactosVigentesProyectoFinder;
    private final PaquetesProyectoGradoFinder paquetesProyectoGradoFinder;
    private final GenerarEntregableProyectoGradoValidator generarEntregableProyectoGradoValidator;
    private final ConfirmarGeneracionEntregableProyectoGradoValidator confirmarGeneracionValidator;
    private final AgregarArtefactosEntregableUseCase agregarArtefactosEntregableUseCase;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    private record Lectura(
            ProyectoGradoDomain proyecto,
            boolean asesorVigente,
            UUID asesorVigenteProyecto,
            boolean existeVigente,
            int ultimaVersion,
            List<ArtefactoDomain> artefactosVigentes) {}

    @Override
    public UUID ejecutar(GeneracionEntregableProyectoGradoDomain entrada) {
        logger.info(EntregableProyectoGradoKey.LOG_GENERANDO, entrada.getProyectoGrado(), entrada.getAsesor());

        var primera = ejecutorTransaccionalOutputPort.enLectura(() -> leer(
                entrada, proyectoGradoPorIdFinder.obtener(entrada.getProyectoGrado())));
        registrarVerificacion(primera);

        generarEntregableProyectoGradoValidator.validar(entrada, primera.proyecto(), primera.asesorVigente(),
                primera.asesorVigenteProyecto(), primera.existeVigente(), primera.artefactosVigentes());

        var entregable = EntregableProyectoGradoDomain.crear(
                entrada.getProyectoGrado(), primera.ultimaVersion(), entrada.getAsesor());
        var generadoEn = UtilFecha.generarInstanteActual();
        var archivo = armadoPaqueteOutputPort.armar(PaqueteEntregableMapper.toContenido(
                entregable, primera.proyecto(), primera.artefactosVigentes(), generadoEn));

        var generado = ejecutorTransaccionalOutputPort.enEscritura(
                () -> confirmar(entrada, primera, entregable, archivo, generadoEn));

        logger.info(EntregableProyectoGradoKey.LOG_GENERADO,
                generado, entregable.getProyectoGrado(), entregable.getVersion());

        return generado;
    }

    private UUID confirmar(GeneracionEntregableProyectoGradoDomain entrada, Lectura primera,
                           EntregableProyectoGradoDomain entregable, ArchivoPaqueteArmado archivo,
                           Instant generadoEn) {
        armadoPaqueteOutputPort.eliminarSiRevierte(archivo.bucket(), archivo.claveObjeto());

        var segunda = leer(entrada, proyectoGradoBloqueadoPorIdFinder.obtener(entrada.getProyectoGrado()));
        var paquetesAnteriores = paquetesProyectoGradoFinder.obtener(entrada.getProyectoGrado());
        registrarVerificacion(segunda);

        generarEntregableProyectoGradoValidator.validar(entrada, segunda.proyecto(), segunda.asesorVigente(),
                segunda.asesorVigenteProyecto(), segunda.existeVigente(), segunda.artefactosVigentes());
        confirmarGeneracionValidator.validar(segunda.proyecto(), primera.ultimaVersion(), segunda.ultimaVersion(),
                versionesDe(primera.artefactosVigentes()), versionesDe(segunda.artefactosVigentes()));

        entregableProyectoGradoOutputPort.registrar(EntregableProyectoGradoMapper.toEntity(entregable));
        var estadoInicial = EstadoEntregableProyectoGradoDomain.crear(entregable.getId());
        estadoEntregableProyectoGradoOutputPort.registrar(EstadoEntregableProyectoGradoMapper.toEntity(estadoInicial));

        var paquete = PaqueteEntregableDomain.crear(
                entregable.getId(), archivo.bucket(), archivo.claveObjeto(), archivo.nombreArchivo(),
                archivo.tamanoBytes(), generadoEn);
        paqueteEntregableOutputPort.guardar(PaqueteEntregableMapper.toEntity(paquete));
        agregarArtefactosEntregableUseCase.ejecutar(AgregarArtefactosEntregableMapper.toDomain(
                entregable.getId(), segunda.artefactosVigentes(), archivo.entradas()));

        paquetesAnteriores.forEach(anterior ->
                armadoPaqueteOutputPort.eliminarTrasConfirmar(anterior.getBucket(), anterior.getClaveObjeto()));

        eventPublisher.publish(new EntregableProyectoGradoGeneradoEvent(
                entregable.getId(), entregable.getProyectoGrado(), entregable.getVersion()));

        return entregable.getId();
    }

    private Lectura leer(GeneracionEntregableProyectoGradoDomain entrada, ProyectoGradoDomain proyecto) {
        return new Lectura(
                proyecto,
                asesorVigenteFinder.obtener(entrada.getAsesor()),
                asesorVigenteProyectoGradoFinder.obtener(entrada.getProyectoGrado()),
                entregableVigenteExisteFinder.obtener(entrada.getProyectoGrado()),
                ultimaVersionEntregableProyectoGradoFinder.obtener(entrada.getProyectoGrado()),
                artefactosVigentesProyectoFinder.obtener(entrada.getProyectoGrado()));
    }

    private void registrarVerificacion(Lectura lectura) {
        logger.debug(EntregableProyectoGradoKey.LOG_VERIFICACION_GENERAR,
                !lectura.proyecto().esVacio(), lectura.proyecto().getEstadoProyecto().getId(),
                lectura.asesorVigente(), !UtilUUID.esPorDefecto(lectura.asesorVigenteProyecto()),
                lectura.existeVigente(), lectura.ultimaVersion(), lectura.artefactosVigentes().size());
    }

    private static Set<UUID> versionesDe(List<ArtefactoDomain> artefactos) {
        return artefactos.stream().map(ArtefactoDomain::getVersionArtefacto).collect(Collectors.toSet());
    }
}
