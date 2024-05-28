package com.demo.seguridad.modelo;

import java.util.List;

public class Usuario {
    private Integer id;
    private String nombre;
    private String celular;
    private String correo;
    private List<Rol> roles;
    private Organizacion organizacion;
    private EstadoUsuario estado;
}
