package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.arquisoft.shared.util.UtilObjeto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;
import java.util.Optional;

@ConfigurationProperties("security.authentication")
public record AutenticacionProperties(List<Emisor> issuers) {

    public AutenticacionProperties {
        issuers = UtilObjeto.esNulo(issuers) ? List.of() : List.copyOf(issuers);
    }

    public List<Emisor> habilitados() {
        return issuers.stream().filter(Emisor::enabled).toList();
    }

    public Optional<Emisor> porId(String id) {
        return habilitados().stream().filter(emisor -> emisor.id().equals(id)).findFirst();
    }

    public Optional<Emisor> porIssuer(String issuer) {
        return habilitados().stream().filter(emisor -> emisor.issuer().equals(issuer)).findFirst();
    }

    public record Emisor(
            String id,
            @DefaultValue("true") boolean enabled,
            @DefaultValue("") String issuer,
            @DefaultValue("") String jwksUri,
            List<String> audiences,
            @DefaultValue("pep") ModoAutorizacion authorization,
            @DefaultValue("false") boolean blacklist,
            @DefaultValue ClaimsIdentidad claims) {

        public Emisor {
            audiences = UtilObjeto.esNulo(audiences) ? List.of() : audiences.stream().filter(a -> !a.isBlank()).toList();
        }
    }
}
