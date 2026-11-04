package com.arquisoft.mapas_ruta.application.maparuta.command.validator.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.validator.CambiarFechaFinMapaRutaValidator;
import com.arquisoft.mapas_ruta.domain.maparuta.CambioFechaFinMapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.model.ExistenciaMapaRuta;
import com.arquisoft.mapas_ruta.domain.maparuta.model.RangoFechasMapaRuta;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.FechaFinPosteriorAInicioRule;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.MapaRutaExisteRule;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.impl.FechaFinPosteriorAInicioRuleImpl;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.impl.MapaRutaExisteRuleImpl;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.PropiedadProyectoGrado;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.CoordinadorPropietarioProyectoGradoRule;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.ProyectoGradoEnProcesoRule;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl.CoordinadorPropietarioProyectoGradoRuleImpl;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl.ProyectoGradoEnProcesoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarFechaFinMapaRutaValidatorImpl implements CambiarFechaFinMapaRutaValidator {

    private final MapaRutaExisteRule mapaRutaExisteRule;
    private final CoordinadorPropietarioProyectoGradoRule coordinadorPropietarioProyectoGradoRule;
    private final ProyectoGradoEnProcesoRule proyectoGradoEnProcesoRule;
    private final FechaFinPosteriorAInicioRule fechaFinPosteriorAInicioRule;

    public CambiarFechaFinMapaRutaValidatorImpl() {
        this.mapaRutaExisteRule = new MapaRutaExisteRuleImpl();
        this.coordinadorPropietarioProyectoGradoRule = new CoordinadorPropietarioProyectoGradoRuleImpl();
        this.proyectoGradoEnProcesoRule = new ProyectoGradoEnProcesoRuleImpl();
        this.fechaFinPosteriorAInicioRule = new FechaFinPosteriorAInicioRuleImpl();
    }

    @Override
    public void validar(CambioFechaFinMapaRutaDomain cambio, MapaRutaDomain mapaRuta, ProyectoGradoDomain proyectoGrado) {
        mapaRutaExisteRule.validar(new ExistenciaMapaRuta(cambio.getMapaRuta(), !mapaRuta.esVacio()));
        coordinadorPropietarioProyectoGradoRule.validar(new PropiedadProyectoGrado(
                mapaRuta.getProyectoGrado(), proyectoGrado.getTituloProyecto(), cambio.getCoordinador(), proyectoGrado.getCoordinador()));
        proyectoGradoEnProcesoRule.validar(new EstadoActualProyectoGrado(
                mapaRuta.getProyectoGrado(), proyectoGrado.getTituloProyecto(), proyectoGrado.getEstadoProyectoGrado()));
        fechaFinPosteriorAInicioRule.validar(new RangoFechasMapaRuta(
                mapaRuta.getId(), mapaRuta.getFechaInicio(), cambio.getFechaFin()));
    }
}
