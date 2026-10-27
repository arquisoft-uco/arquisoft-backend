package com.arquisoft.mapas_ruta.application.tarea.command.validator.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.validator.AsignarTareaValidator;
import com.arquisoft.mapas_ruta.domain.asignacionasesor.model.AsignacionAsesorProyectoGrado;
import com.arquisoft.mapas_ruta.domain.asignacionasesor.rules.AsesorAsignadoProyectoGradoRule;
import com.arquisoft.mapas_ruta.domain.asignacionasesor.rules.impl.AsesorAsignadoProyectoGradoRuleImpl;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.model.ExistenciaMapaRuta;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.MapaRutaExisteRule;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.impl.MapaRutaExisteRuleImpl;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.ProyectoGradoEnProcesoRule;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl.ProyectoGradoEnProcesoRuleImpl;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.model.DisponibilidadTarea;
import com.arquisoft.mapas_ruta.domain.tarea.rules.TareaUnicaPorMapaRutaRule;
import com.arquisoft.mapas_ruta.domain.tarea.rules.impl.TareaUnicaPorMapaRutaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class AsignarTareaValidatorImpl implements AsignarTareaValidator {

    private final MapaRutaExisteRule mapaRutaExisteRule;
    private final TareaUnicaPorMapaRutaRule tareaUnicaPorMapaRutaRule;
    private final ProyectoGradoEnProcesoRule proyectoGradoEnProcesoRule;
    private final AsesorAsignadoProyectoGradoRule asesorAsignadoProyectoGradoRule;

    public AsignarTareaValidatorImpl() {
        this.mapaRutaExisteRule = new MapaRutaExisteRuleImpl();
        this.tareaUnicaPorMapaRutaRule = new TareaUnicaPorMapaRutaRuleImpl();
        this.proyectoGradoEnProcesoRule = new ProyectoGradoEnProcesoRuleImpl();
        this.asesorAsignadoProyectoGradoRule = new AsesorAsignadoProyectoGradoRuleImpl();
    }

    @Override
    public void validar(TareaDomain tarea, MapaRutaDomain mapaRuta, ProyectoGradoDomain proyectoGrado,
                        boolean tareaExiste, boolean asesorAsignado) {
        mapaRutaExisteRule.validar(new ExistenciaMapaRuta(tarea.getMapaRuta(), !mapaRuta.esVacio()));
        tareaUnicaPorMapaRutaRule.validar(new DisponibilidadTarea(
                tarea.getMapaRuta(), tarea.getDescripcion(), tareaExiste));
        proyectoGradoEnProcesoRule.validar(new EstadoActualProyectoGrado(
                mapaRuta.getProyectoGrado(), proyectoGrado.getTituloProyecto(), proyectoGrado.getEstadoProyectoGrado()));
        asesorAsignadoProyectoGradoRule.validar(new AsignacionAsesorProyectoGrado(
                mapaRuta.getProyectoGrado(), proyectoGrado.getTituloProyecto(), tarea.getAsesor(), asesorAsignado));
    }
}
