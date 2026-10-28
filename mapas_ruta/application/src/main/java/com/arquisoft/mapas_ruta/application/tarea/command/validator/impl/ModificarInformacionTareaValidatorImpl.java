package com.arquisoft.mapas_ruta.application.tarea.command.validator.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.validator.ModificarInformacionTareaValidator;
import com.arquisoft.mapas_ruta.domain.asignacionasesor.model.AsignacionAsesorProyectoGrado;
import com.arquisoft.mapas_ruta.domain.asignacionasesor.rules.AsesorAsignadoProyectoGradoRule;
import com.arquisoft.mapas_ruta.domain.asignacionasesor.rules.impl.AsesorAsignadoProyectoGradoRuleImpl;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.model.ExistenciaMapaRuta;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.MapaRutaExisteRule;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.impl.MapaRutaExisteRuleImpl;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.tarea.ModificacionTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.model.DisponibilidadTarea;
import com.arquisoft.mapas_ruta.domain.tarea.model.ExistenciaTarea;
import com.arquisoft.mapas_ruta.domain.tarea.rules.TareaExisteRule;
import com.arquisoft.mapas_ruta.domain.tarea.rules.TareaUnicaPorMapaRutaRule;
import com.arquisoft.mapas_ruta.domain.tarea.rules.impl.TareaExisteRuleImpl;
import com.arquisoft.mapas_ruta.domain.tarea.rules.impl.TareaUnicaPorMapaRutaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ModificarInformacionTareaValidatorImpl implements ModificarInformacionTareaValidator {

    private final TareaExisteRule tareaExisteRule;
    private final MapaRutaExisteRule mapaRutaExisteRule;
    private final TareaUnicaPorMapaRutaRule tareaUnicaPorMapaRutaRule;
    private final AsesorAsignadoProyectoGradoRule asesorAsignadoProyectoGradoRule;

    public ModificarInformacionTareaValidatorImpl() {
        this.tareaExisteRule = new TareaExisteRuleImpl();
        this.mapaRutaExisteRule = new MapaRutaExisteRuleImpl();
        this.tareaUnicaPorMapaRutaRule = new TareaUnicaPorMapaRutaRuleImpl();
        this.asesorAsignadoProyectoGradoRule = new AsesorAsignadoProyectoGradoRuleImpl();
    }

    @Override
    public void validar(ModificacionTareaDomain modificacion, TareaDomain tarea, MapaRutaDomain mapaRuta,
                        ProyectoGradoDomain proyectoGrado,
                        boolean descripcionEnUso, boolean asesorAsignado) {
        tareaExisteRule.validar(new ExistenciaTarea(modificacion.getTarea(), !tarea.esVacio()));
        mapaRutaExisteRule.validar(new ExistenciaMapaRuta(tarea.getMapaRuta(), !mapaRuta.esVacio()));
        tareaUnicaPorMapaRutaRule.validar(new DisponibilidadTarea(
                tarea.getMapaRuta(), modificacion.getDescripcion(), descripcionEnUso));
        asesorAsignadoProyectoGradoRule.validar(new AsignacionAsesorProyectoGrado(
                mapaRuta.getProyectoGrado(), proyectoGrado.getTituloProyecto(), modificacion.getAsesor(), asesorAsignado));
    }
}
