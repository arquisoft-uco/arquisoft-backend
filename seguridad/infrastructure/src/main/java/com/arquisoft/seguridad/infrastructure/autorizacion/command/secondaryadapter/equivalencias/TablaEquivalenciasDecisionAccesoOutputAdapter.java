package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.DecisionAccesoOutputPort;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ExtractorClaims;
import com.arquisoft.shared.util.UtilObjeto;
import com.nimbusds.jwt.JWTParser;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class TablaEquivalenciasDecisionAccesoOutputAdapter implements DecisionAccesoOutputPort {

    private final Map<String, String> rolPorRecurso = new HashMap<>();
    private final AutenticacionProperties propiedadesAutenticacion;

    public TablaEquivalenciasDecisionAccesoOutputAdapter(EquivalenciasProperties equivalencias,
                                                         AutenticacionProperties autenticacion) {
        equivalencias.entries().forEach(entrada -> rolPorRecurso.put(entrada.clave(), entrada.role()));
        this.propiedadesAutenticacion = autenticacion;
    }

    @Override
    public DecisionAcceso decidir(SolicitudAcceso solicitud) {
        var rolExigido = rolPorRecurso.get(solicitud.metodo().toUpperCase(Locale.ROOT) + " " + solicitud.recurso());
        if (UtilObjeto.esNulo(rolExigido)) {
            return new DecisionAcceso.Denegada();
        }
        return permisosDelToken(solicitud).contains(rolExigido)
                ? new DecisionAcceso.Permitida("")
                : new DecisionAcceso.Denegada();
    }

    private List<String> permisosDelToken(SolicitudAcceso solicitud) {
        var emisor = propiedadesAutenticacion.porId(solicitud.emisor());
        if (emisor.isEmpty()) {
            return List.of();
        }
        try {
            var claims = JWTParser.parse(solicitud.credencial()).getJWTClaimsSet().getClaims();
            return ExtractorClaims.lista(claims, emisor.get().claims().permisos());
        } catch (ParseException e) {
            return List.of();
        }
    }
}
