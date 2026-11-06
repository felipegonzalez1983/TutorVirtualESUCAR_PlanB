# Tutor Virtual ESUCAR

**Desarrollo de Aplicaciones Móviles — DSY1105**

**Duoc UC — Escuela de Informática y Telecomunicaciones**

## 1. Descripción del proyecto

Tutor Virtual ESUCAR es un prototipo de aplicación móvil Android desarrollado para apoyar a los estudiantes de la Escuela de Suboficiales de Carabineros de Chile (ESUCAR) durante las asignaturas Trabajo de Título I, II y III.

La aplicación busca facilitar el acceso a guías metodológicas, preguntas frecuentes, normas APA, seguimiento de avances y recursos académicos, contribuyendo a la autonomía del estudiante.

## 2. Equipo de desarrollo

| Integrante | Responsabilidad |
|---|---|
| Felipe González | Configuración inicial, arquitectura, navegación e integración |
| Yanina Silva | Diseño de interfaces, formularios y accesibilidad |
| Vicente Valenzuela | Persistencia local, seguimiento de entregables y recursos nativos |

*Las responsabilidades indicadas corresponden a la distribución propuesta del equipo.*

## 3. Tecnologías utilizadas

- Kotlin
- Jetpack Compose
- Material 3
- Android Studio
- Git y GitHub
- Estructura inicial orientada a MVVM

## 4. Funcionalidades implementadas

- Pantalla principal con identidad visual propuesta para ESUCAR.
- Acceso visual a seis secciones académicas.
- Navegación básica funcional entre pantallas provisionales.
- Botón para regresar al inicio.
- Ejecución verificada en emulador Pixel 7 con Android 15.

## 5. Funcionalidades pendientes

- Guías metodológicas de Trabajo de Título I, II y III.
- Buscador de preguntas frecuentes.
- Consulta de normas APA.
- Seguimiento y almacenamiento local de entregables.
- Calendario y notificaciones académicas.
- Integración de recursos nativos.
- Validaciones de formularios y animaciones.
- Pruebas de funcionamiento e integración.

## 6. Arquitectura del proyecto

```text
cl.duoc.tutorvirtual
├── data
├── model
├── navigation
│   └── AppNavigation.kt
├── ui
│   ├── components
│   ├── screens
│   │   └── HomeScreen.kt
│   └── theme
├── viewmodel
└── MainActivity.kt
```

La implementación completa del patrón MVVM se encuentra pendiente.

## 7. Cómo ejecutar la aplicación

1. Clonar el repositorio de GitHub.
2. Abrir el proyecto en Android Studio.
3. Sincronizar las dependencias de Gradle.
4. Seleccionar un emulador Android compatible.
5. Ejecutar la configuración `app`.

**Repositorio:** https://github.com/felipegonzalez1983/Tutor_Virtual_ESUCAR

## 8. Estado del proyecto

Prototipo académico en desarrollo. Las pantallas secundarias actuales son provisionales y se reemplazarán por sus funcionalidades correspondientes.

Esta aplicación no constituye un sistema oficial de Carabineros de Chile ni implica su adopción institucional.