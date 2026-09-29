package com.arquisoft.mapas_ruta.application.maparuta.command.validator.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.validator.AgregarMapaRutaValidator;
import com.arquisoft.mapas_ruta.domain.maparuta.AgregacionMapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.model.DisponibilidadMapaRuta;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.MapaRutaUnicoPorProyectoRule;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.impl.MapaRutaUnicoPorProyectoRuleImpl;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.PropiedadProyectoGrado;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.CoordinadorPropietarioProyectoGradoRule;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.ProyectoGradoEnProcesoRule;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.ProyectoGradoExisteRule;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl.CoordinadorPropietarioProyectoGradoRuleImpl;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl.ProyectoGradoEnProcesoRuleImpl;
import com.arquisoft.mapas_ruta.domain.proyectogrado.rules.impl.ProyectoGradoExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class AgregarMapaRutaValidatorImpl implements AgregarMapaRutaValidator {

    private final ProyectoGradoExisteRule proyectoGradoExisteRule;
    private final CoordinadorPropietarioProyectoGradoRule coordinadorPropietarioProyectoGradoRule;
    private final ProyectoGradoEnProcesoRule proyectoGradoEnProcesoRule;
    private final MapaRutaUnicoPorProyectoRule mapaRutaUnicoPorProyectoRule;

    public AgregarMapaRutaValidatorImpl() {
        this.proyectoGradoExisteRule = new ProyectoGradoExisteRuleImpl();
        this.coordinadorPropietarioProyectoGradoRule = new CoordinadorPropietarioProyectoGradoRuleImpl();
        this.proyectoGradoEnProcesoRule = new ProyectoGradoEnProcesoRuleImpl();
        this.mapaRutaUnicoPorProyectoRule = new MapaRutaUnicoPorProyectoRuleImpl();
    }

    @Override
    public void validar(AgregacionMapaRutaDomain agregacion, ProyectoGradoDomain proyectoGrado, boolean mapaRutaExiste) {
        var proyectoGradoId = agregacion.getMapaRuta().getProyectoGrado();

        proyectoGradoExisteRule.validar(new ExistenciaProyectoGrado(proyectoGradoId, !proyectoGrado.esVacio()));
        coordinadorPropietarioProyectoGradoRule.validar(new PropiedadProyectoGrado(
                proyectoGradoId, agregacion.getCoordinador(), proyectoGrado.getCoordinador()));
        proyectoGradoEnProcesoRule.validar(new EstadoActualProyectoGrado(
                proyectoGradoId, proyectoGrado.getEstadoProyectoGrado()));
        mapaRutaUnicoPorProyectoRule.validar(new DisponibilidadMapaRuta(proyectoGradoId, mapaRutaExiste));
    }
}
