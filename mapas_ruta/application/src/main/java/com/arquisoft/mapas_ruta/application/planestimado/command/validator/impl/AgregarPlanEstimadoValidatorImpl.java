package com.arquisoft.mapas_ruta.application.planestimado.command.validator.impl;

import com.arquisoft.mapas_ruta.application.planestimado.command.validator.AgregarPlanEstimadoValidator;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.model.ExistenciaMapaRuta;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.MapaRutaExisteRule;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.impl.MapaRutaExisteRuleImpl;
import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;
import com.arquisoft.mapas_ruta.domain.planestimado.model.DisponibilidadPlanEstimado;
import com.arquisoft.mapas_ruta.domain.planestimado.model.PlazoPlanesEstimados;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.PlanEstimadoUnicoPorTareaRule;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.TiempoPlanesDentroDelPlazoRule;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.impl.PlanEstimadoUnicoPorTareaRuleImpl;
import com.arquisoft.mapas_ruta.domain.planestimado.rules.impl.TiempoPlanesDentroDelPlazoRuleImpl;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.model.ExistenciaTarea;
import com.arquisoft.mapas_ruta.domain.tarea.rules.TareaExisteRule;
import com.arquisoft.mapas_ruta.domain.tarea.rules.impl.TareaExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class AgregarPlanEstimadoValidatorImpl implements AgregarPlanEstimadoValidator {

    private final TareaExisteRule tareaExisteRule;
    private final MapaRutaExisteRule mapaRutaExisteRule;
    private final PlanEstimadoUnicoPorTareaRule planEstimadoUnicoPorTareaRule;
    private final TiempoPlanesDentroDelPlazoRule tiempoPlanesDentroDelPlazoRule;

    public AgregarPlanEstimadoValidatorImpl() {
        this.tareaExisteRule = new TareaExisteRuleImpl();
        this.mapaRutaExisteRule = new MapaRutaExisteRuleImpl();
        this.planEstimadoUnicoPorTareaRule = new PlanEstimadoUnicoPorTareaRuleImpl();
        this.tiempoPlanesDentroDelPlazoRule = new TiempoPlanesDentroDelPlazoRuleImpl();
    }

    @Override
    public void validar(PlanEstimadoDomain planEstimado, TareaDomain tarea, MapaRutaDomain mapaRuta,
                        boolean planExiste, long diasAcumulados) {
        tareaExisteRule.validar(new ExistenciaTarea(planEstimado.getTarea(), !tarea.esVacio()));
        mapaRutaExisteRule.validar(new ExistenciaMapaRuta(tarea.getMapaRuta(), !mapaRuta.esVacio()));
        planEstimadoUnicoPorTareaRule.validar(new DisponibilidadPlanEstimado(
                planEstimado.getTarea(), tarea.getDescripcion(), planExiste));
        tiempoPlanesDentroDelPlazoRule.validar(new PlazoPlanesEstimados(
                mapaRuta.getId(), mapaRuta.getFechaInicio(), mapaRuta.getFechaFin(),
                diasAcumulados, planEstimado.getTiempoEstimadoDias()));
    }
}
