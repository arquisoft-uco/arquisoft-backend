package com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.entity;

import java.util.UUID;

public record EntregableProyectoGradoEntity(UUID id, UUID proyectoGrado, int versionEntregable,
                                            String estadoEntregable, String claveObjeto, String nombreArchivo) {}
