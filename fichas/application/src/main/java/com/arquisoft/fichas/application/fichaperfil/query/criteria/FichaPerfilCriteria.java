package com.arquisoft.fichas.application.fichaperfil.query.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FichaPerfilCriteria {

    private final int pagina;
    private final int tamanio;
    private final List<SortOrder> ordenamiento;

    // --- filtros individuales (se combinan con AND entre ellos) ---
    private final String tituloProyecto;   // LIKE %valor%
    private final UUID   asesorId;         // exacto
    private final String asesorNombre;     // LIKE %valor%
    private final String asesorEmail;      // LIKE %valor%

    // --- búsqueda global (OR interno: coincide en título O asesor) ---
    private final String termino;          // LIKE %valor% en título OR nombre asesor

    private FichaPerfilCriteria(Builder builder) {
        this.pagina         = Math.max(0, builder.pagina);
        this.tamanio        = builder.tamanio > 0 ? builder.tamanio : 10;
        this.ordenamiento   = List.copyOf(builder.ordenamiento);
        this.tituloProyecto = builder.tituloProyecto;
        this.asesorId       = builder.asesorId;
        this.asesorNombre   = builder.asesorNombre;
        this.asesorEmail    = builder.asesorEmail;
        this.termino        = builder.termino;
    }

    public static Builder builder() { return new Builder(); }

    public int getPagina()                   { return pagina; }
    public int getTamanio()                  { return tamanio; }
    public List<SortOrder> getOrdenamiento() { return ordenamiento; }
    public String getTituloProyecto()        { return tituloProyecto; }
    public UUID getAsesorId()                { return asesorId; }
    public String getAsesorNombre()          { return asesorNombre; }
    public String getAsesorEmail()           { return asesorEmail; }
    public String getTermino()               { return termino; }
    public boolean tieneOrden()              { return !ordenamiento.isEmpty(); }

    public static final class Builder {
        private int pagina;
        private int tamanio = 10;
        private List<SortOrder> ordenamiento = new ArrayList<>();
        private String tituloProyecto;
        private UUID   asesorId;
        private String asesorNombre;
        private String asesorEmail;
        private String termino;

        public Builder pagina(int pagina)                        { this.pagina = pagina; return this; }
        public Builder tamanio(int tamanio)                      { this.tamanio = tamanio; return this; }
        public Builder ordenamiento(List<SortOrder> ordenamiento){ this.ordenamiento = ordenamiento != null ? ordenamiento : new ArrayList<>(); return this; }
        public Builder tituloProyecto(String titulo)             { this.tituloProyecto = titulo; return this; }
        public Builder asesorId(UUID id)                         { this.asesorId = id; return this; }
        public Builder asesorNombre(String nombre)               { this.asesorNombre = nombre; return this; }
        public Builder asesorEmail(String email)                 { this.asesorEmail = email; return this; }
        public Builder termino(String termino)                   { this.termino = termino; return this; }
        public FichaPerfilCriteria build()                       { return new FichaPerfilCriteria(this); }
    }
}
