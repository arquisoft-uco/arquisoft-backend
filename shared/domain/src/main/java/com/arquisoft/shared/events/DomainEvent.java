package com.arquisoft.shared.events;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class DomainEvent {

    private static final Pattern PATRON_TEMA = Pattern.compile("^[a-z][a-z_]*\\.[a-z][a-z_]*\\.[a-z][a-z_]*$");

    private final String idEvento;
    private final Instant ocurridoEn;
    private final String tipoEvento;
    private final String temaEvento;

    protected DomainEvent(String temaEvento, String tipoEvento) {
        validarTema(temaEvento);
        this.idEvento = UUID.randomUUID().toString();
        this.ocurridoEn = Instant.now();
        this.tipoEvento = tipoEvento;
        this.temaEvento = temaEvento;
    }

    private static void validarTema(String tema) {
        if (tema == null || !PATRON_TEMA.matcher(tema).matches()) {
            throw new IllegalArgumentException(
                "El tema del evento '" + tema + "' no cumple el formato requerido '{contexto}.{entidad}.{accion}' "
                + "(ej. 'seguridad.usuario.creado')");
        }
    }

    public String getIdEvento() {
        return idEvento;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public final String getTemaEvento() {
        return temaEvento;
    }
}
