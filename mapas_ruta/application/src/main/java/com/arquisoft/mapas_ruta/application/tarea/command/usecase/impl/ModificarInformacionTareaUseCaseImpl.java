package com.arquisoft.mapas_ruta.application.tarea.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.asignacionasesor.command.finder.AsesorAsignadoProyectoGradoFinder;
import com.arquisoft.mapas_ruta.application.asignacionasesor.command.finder.model.AsesorEnProyectoGrado;
import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapaRutaPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.DescripcionTareaEnUsoFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.model.DescripcionTareaEnMapaRuta;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.mapas_ruta.application.tarea.command.usecase.ModificarInformacionTareaUseCase;
import com.arquisoft.mapas_ruta.application.tarea.command.validator.ModificarInformacionTareaValidator;
import com.arquisoft.mapas_ruta.domain.tarea.ModificacionTareaDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarInformacionTareaUseCaseImpl implements ModificarInformacionTareaUseCase {

    private final TareaOutputPort tareaOutputPort;
    private final TareaPorIdFinder tareaPorIdFinder;
    private final MapaRutaPorIdFinder mapaRutaPorIdFinder;
    private final DescripcionTareaEnUsoFinder descripcionTareaEnUsoFinder;
    private final AsesorAsignadoProyectoGradoFinder asesorAsignadoProyectoGradoFinder;
    private final ModificarInformacionTareaValidator modificarInformacionTareaValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionTareaDomain entrada) {
        logger.info(TareaKey.LOG_MODIFICANDO_INFORMACION, entrada.getTarea(), entrada.getAsesor());

        var tarea = tareaPorIdFinder.obtener(entrada.getTarea());
        var tareaExiste = !tarea.esVacio();
        var mapaRuta = mapaRutaPorIdFinder.obtener(tarea.getMapaRuta());
        var mapaRutaExiste = !mapaRuta.esVacio();
        var descripcionEnUso = tareaExiste && descripcionTareaEnUsoFinder.obtener(
                new DescripcionTareaEnMapaRuta(entrada.getTarea(), tarea.getMapaRuta(), entrada.getDescripcion()));
        var asesorAsignado = mapaRutaExiste && asesorAsignadoProyectoGradoFinder.obtener(
                new AsesorEnProyectoGrado(mapaRuta.getProyectoGrado(), entrada.getAsesor()));

        logger.debug(TareaKey.LOG_VERIFICACION_MODIFICAR_INFORMACION,
                tareaExiste, mapaRutaExiste, descripcionEnUso, asesorAsignado);
        modificarInformacionTareaValidator.validar(entrada, tarea, mapaRuta, descripcionEnUso, asesorAsignado);

        tareaOutputPort.actualizarInformacion(entrada.getTarea(), entrada.getDescripcion(), entrada.getAsesor());

        logger.info(TareaKey.LOG_INFORMACION_MODIFICADA, entrada.getTarea(), tarea.getMapaRuta());
    }
}
