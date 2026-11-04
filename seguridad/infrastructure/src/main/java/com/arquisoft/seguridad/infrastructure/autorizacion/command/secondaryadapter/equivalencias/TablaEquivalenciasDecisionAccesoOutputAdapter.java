package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.DecisionAccesoOutputPort;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.RolClienteOtorgadoOutputPort;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ExtractorClaims;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
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
    private final RolClienteOtorgadoOutputPort rolClienteOtorgadoOutputPort;

    public TablaEquivalenciasDecisionAccesoOutputAdapter(EquivalenciasProperties equivalencias,
                                                         AutenticacionProperties autenticacion,
                                                         RolClienteOtorgadoOutputPort rolClienteOtorgadoOutputPort) {
        equivalencias.entries().forEach(entrada -> rolPorRecurso.put(entrada.clave(), entrada.role()));
        this.propiedadesAutenticacion = autenticacion;
        this.rolClienteOtorgadoOutputPort = rolClienteOtorgadoOutputPort;
    }

    @Override
    public DecisionAcceso decidir(SolicitudAcceso solicitud) {
        var rolExigido = rolPorRecurso.get(solicitud.metodo().toUpperCase(Locale.ROOT) + " " + solicitud.recurso());
        if (UtilObjeto.esNulo(rolExigido)) {
            return new DecisionAcceso.Denegada();
        }
        var rolesRealm = rolesRealmDelToken(solicitud);
        if (rolesRealm.isEmpty()) {
            return new DecisionAcceso.Denegada();
        }
        return rolClienteOtorgadoOutputPort.rolClienteOtorgadoPor(rolExigido, rolesRealm)
                ? new DecisionAcceso.Permitida(UtilTexto.VACIO)
                : new DecisionAcceso.Denegada();
    }

    private List<String> rolesRealmDelToken(SolicitudAcceso solicitud) {
        var emisor = propiedadesAutenticacion.porId(solicitud.emisor());
        if (emisor.isEmpty()) {
            return List.of();
        }
        try {
            var claims = JWTParser.parse(solicitud.credencial()).getJWTClaimsSet().getClaims();
            return ExtractorClaims.lista(claims, emisor.get().claims().roles());
        } catch (ParseException e) {
            return List.of();
        }
    }
}
