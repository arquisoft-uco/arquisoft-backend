package com.demo.seguridad.modelo;

public class CredencialRegistro {
    private String nombreUsuario;
    private String clave;
    private Usuario usuario;

    private CredencialRegistro(Builder builder) {
        this.nombreUsuario = builder.nombreUsuario;
        this.clave = builder.clave;
        this.usuario = builder.usuario;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .nombreUsuario(this.nombreUsuario)
                .clave(this.clave)
                .usuario(this.usuario);
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public static class Builder {
        private String nombreUsuario;
        private String clave;
        private Usuario usuario;

        public Builder nombreUsuario(String nombreUsuario) {
            this.nombreUsuario = nombreUsuario;
            return this;
        }

        public Builder clave(String clave) {
            this.clave = clave;
            return this;
        }

        public Builder usuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public Builder ejecutarValidaciones() {
            return this;
        }

        public CredencialRegistro construir() {
            return new CredencialRegistro(this);
        }

    }

}
