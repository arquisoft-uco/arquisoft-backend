package com.arquisoft.fichas.application.fichaperfil.query.criteria;

import com.arquisoft.shared.pagination.SortDirection;

import java.util.UUID;

public final class FichaPerfilCriteria {

    private final int pagina;
    private final int tamanio;
    private final String ordenarPor;
    private final SortDirection direccion;
    private final String tituloProyecto;
    private final UUID asesorId;

    private FichaPerfilCriteria(Builder builder) {
        this.pagina = Math.max(0, builder.pagina);
        this.tamanio = builder.tamanio > 0 ? builder.tamanio : 10;
        this.ordenarPor = builder.ordenarPor;
        this.direccion = builder.direccion != null ? builder.direccion : SortDirection.ASC;
        this.tituloProyecto = builder.tituloProyecto;
        this.asesorId = builder.asesorId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getPagina()              { return pagina; }
    public int getTamanio()             { return tamanio; }
    public String getOrdenarPor()       { return ordenarPor; }
    public SortDirection getDireccion() { return direccion; }
    public String getTituloProyecto()   { return tituloProyecto; }
    public UUID getAsesorId()           { return asesorId; }
    public boolean tieneOrden()         { return ordenarPor != null && !ordenarPor.isBlank(); }

    public static final class Builder {
        private int pagina;
        private int tamanio = 10;
        private String ordenarPor;
        private SortDirection direccion;
        private String tituloProyecto;
        private UUID asesorId;

        public Builder pagina(int pagina)                  { this.pagina = pagina; return this; }
        public Builder tamanio(int tamanio)                { this.tamanio = tamanio; return this; }
        public Builder ordenarPor(String campo)            { this.ordenarPor = campo; return this; }
        public Builder direccion(SortDirection direccion)  { this.direccion = direccion; return this; }
        public Builder tituloProyecto(String titulo)       { this.tituloProyecto = titulo; return this; }
        public Builder asesorId(UUID id)                   { this.asesorId = id; return this; }
        public FichaPerfilCriteria build()                 { return new FichaPerfilCriteria(this); }
    }
}
