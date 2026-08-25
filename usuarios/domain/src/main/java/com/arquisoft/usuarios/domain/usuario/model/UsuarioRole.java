package com.arquisoft.usuarios.domain.usuario.model;

import com.arquisoft.usuarios.domain.usuario.exception.RolUsuarioNoEncontradoException;

import java.util.Arrays;
import java.util.Optional;

public enum UsuarioRole {
    ESTUDIANTE("estudiante", "Estudiante que presenta proyecto de grado"),
    ASESOR("asesor", "Asesor asignado a un proyecto de grado"),
    ASESOR_FICHA("asesor-ficha", "Asesor que apoya la elaboracion de fichas de perfil"),
    COORDINADOR("coordinador", "Coordinador del programa que gestiona proyectos"),
    JURADO("jurado", "Jurado que evalua proyectos de grado"),
    BIBLIOTECARIO("bibliotecario", "Bibliotecario que gestiona consulta de PG"),
    REPRESENTANTE_COMITE_CURRICULUM("representante-comite", "Representante que aprueba fichas de perfil"),
    ADMINISTRADOR("administrador", "Administrador del sistema");

    private final String codigo;
    private final String descripcion;

    UsuarioRole(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getId() {
        return name();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static UsuarioRole desdeCodigo(String codigo) {
        return delCatalogo(codigo).orElseThrow(() -> new RolUsuarioNoEncontradoException(codigo));
    }

    public static boolean esCodigoValido(String codigo) {
        return delCatalogo(codigo).isPresent();
    }

    private static Optional<UsuarioRole> delCatalogo(String codigo) {
        return Arrays.stream(values())
                .filter(rol -> rol.codigo.equalsIgnoreCase(codigo))
                .findFirst();
    }
}
