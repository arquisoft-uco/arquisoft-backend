package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.AutorizacionKey;
import com.arquisoft.shared.util.UtilTexto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ValidadorConfiguracionSeguridad {

    private static final String PERFIL_PRODUCCION = "prod";
    private static final String MODO_TEMPLATE = "template";

    private final AppLogger logger;
    private final AutenticacionProperties propiedadesAutenticacion;
    private final PepProperties pep;
    private final RutasPublicas rutasPublicas;
    private final Environment entorno;

    @Value("${security.enabled:true}")
    private boolean seguridadHabilitada;

    @Value("${security.authorization.resource.path-mode:template}")
    private String modoRuta;

    @PostConstruct
    void validar() {
        var problemas = problemas();
        if (!problemas.isEmpty()) {
            throw new IllegalStateException(
                    Mensajes.obtener(AutorizacionKey.ERROR_CONFIGURACION_INSEGURA) + ": " + String.join("; ", problemas));
        }
    }

    List<String> problemas() {
        var problemas = new ArrayList<String>();
        var habilitados = propiedadesAutenticacion.habilitados();
        if (habilitados.isEmpty()) {
            problemas.add("ningun emisor habilitado en security.authentication.issuers");
        }
        var ids = new HashSet<String>();
        for (var emisor : propiedadesAutenticacion.issuers()) {
            if (UtilTexto.esVacioONulo(emisor.id()) || !ids.add(emisor.id())) {
                problemas.add("id de emisor vacio o duplicado: " + emisor.id());
            }
        }
        for (var emisor : habilitados) {
            if (UtilTexto.esVacioONulo(emisor.issuer())) {
                problemas.add("el emisor " + emisor.id() + " no declara issuer");
            }
        }
        var usaPep = habilitados.stream().anyMatch(emisor -> emisor.authorization() == ModoAutorizacion.PEP);
        if (usaPep) {
            problemas.addAll(pep.problemas());
        }
        var usaTabla = habilitados.stream().anyMatch(emisor -> emisor.authorization() == ModoAutorizacion.ROLE_TABLE);
        if (usaTabla && !MODO_TEMPLATE.equalsIgnoreCase(modoRuta)) {
            problemas.add("el modo role-table exige security.authorization.resource.path-mode=template");
        }
        if (entorno.acceptsProfiles(Profiles.of(PERFIL_PRODUCCION))) {
            if (habilitados.stream().anyMatch(emisor -> emisor.authorization() == ModoAutorizacion.NONE)) {
                problemas.add("el modo de autorizacion none no se permite en produccion");
            }
            if (!seguridadHabilitada) {
                problemas.add("security.enabled=false no se permite en produccion");
            }
            if (pep.allowInsecureHttp()) {
                problemas.add("security.authorization.pep.allow-insecure-http=true no se permite en produccion");
            }
        }
        return problemas;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registrarConfiguracionAplicada() {
        var emisores = propiedadesAutenticacion.habilitados().stream()
                .map(AutenticacionProperties.Emisor::id)
                .collect(Collectors.joining(","));
        var modos = propiedadesAutenticacion.habilitados().stream()
                .map(emisor -> emisor.id() + "=" + emisor.authorization())
                .collect(Collectors.joining(","));
        logger.info(AutorizacionKey.LOG_CONFIGURACION_AUTORIZACION, emisores, modos, seguridadHabilitada);
        propiedadesAutenticacion.habilitados().stream()
                .filter(emisor -> emisor.audiences().isEmpty())
                .forEach(emisor -> logger.warn(AutorizacionKey.LOG_AUDIENCIA_SIN_VALIDAR, emisor.id()));
    }
}
