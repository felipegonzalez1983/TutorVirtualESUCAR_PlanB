# Tutor Virtual ESUCAR

App Android para llevar el orden del trabajo de título en la Escuela de Suboficiales (ESUCAR): etapas, entregables, fechas del calendario y evidencias, todo en un solo lugar.

Es el proyecto de la **Evaluación Parcial 2** de **DESARROLLO DE APLICACIONES MOVILES_002V (DSY1105)**, DUOC UC. Profesor: Juan Azocar.

> Es un prototipo académico. No es una aplicación oficial de Carabineros de Chile ni de ESUCAR, y las cuentas de acceso son de prueba.

## Equipo Sivagon

| Integrante | En qué trabajó |
|---|---|
| Felipe González | Base del proyecto, arquitectura, Room y recursos nativos |
| Vicente Valenzuela | Pruebas, documentación y apoyo en el desarrollo |
| Yanina Silva | Diseño de la interfaz, integración y contenidos |

## De qué trata

Los estudiantes de ESUCAR tienen que cumplir etapas y entregas de Trabajo de Título I y II, y 
la información está repartida en programas, calendarios y guías en PDF o Word. La app junta 
todo eso: explica cada etapa, muestra los hitos del calendario 2026, responde dudas 
frecuentes y deja registrar los avances con sus evidencias.

## Qué se puede hacer en la app

**Inicio y navegación**
- Pantalla de inicio con el avance general y accesos rápidos.
- Barra inferior con Inicio, Etapas, Progreso y Más.
- Transiciones suaves entre pantallas y barra de progreso animada.

**Etapas del trabajo**
- Las 5 etapas con objetivo, actividades y formato de entrega.
- Guía paso a paso para cada etapa.
- Estado de cada etapa (por iniciar, en progreso, completado).

**Entregables y formularios**
- Registrar avances con título, descripción, fecha y estado.
- Cada campo tiene ícono, mensaje de error y confirmación cuando está bien.
- Las reglas de validación están en `ValidacionEntregable`, separadas de las pantallas.
- Listado de entregables y cambio de estado.

**Información de apoyo**
- Hitos y fechas del calendario académico 2026, con recordatorios.
- Preguntas frecuentes por capítulo de la guía metodológica.
- Pautas y rúbricas, y novedades académicas.
- Modo de accesibilidad con alto contraste.

**Recursos nativos del teléfono**
- **Biometría:** verificación con huella o rostro en la sección Seguridad biométrica (Más).
- **Cámara:** fotos como evidencia.
- **Selector de documentos:** adjuntar PDF.
- **GPS:** guarda la ubicación del trabajo de campo y la abre en el mapa.
- **Notificaciones:** recordatorios de hitos.

## Cómo está organizado

Usamos MVVM. Dentro de `app/src/main/java/cl/duoc/tutorvirtual`:

```
data/           Room, DataStore, repositorios, ubicación y biometría
model/          Modelos de datos
viewmodel/      Estado de las pantallas y reglas de validación
navigation/     Inicio de sesión y paso a la app
notifications/  Recordatorios
ui/             Pantallas con Jetpack Compose y tema
MainActivity.kt
```

**Datos guardados en el teléfono**
- Room (versión 2): etapas, entregables y evidencias.
- DataStore: sesión, estado de las etapas, accesibilidad y ubicación.

## Para ejecutarla

Necesitas Android Studio reciente con JDK 17 (el que trae por defecto), y un emulador o celular con Android 7.0 (API 24) o superior.

1. Clona el repositorio:
   ```
   git clone https://github.com/felipegonzalez1983/TutorVirtualESUCAR_PlanB.git
   ```
2. Abre la carpeta en Android Studio y espera a que termine la sincronización de Gradle.
3. Elige un emulador o conecta un celular con la depuración USB activada.
4. Presiona **Run** (el botón verde).
5. Ingresa con una cuenta de prueba:

   | Identificador | Contraseña |
   |---|---|
   | DEMO-001 | Demo2026! |
   | DEMO-002 | Demo2026! |
   | DEMO-003 | Demo2026! |

6. Acepta los permisos de cámara, ubicación y notificaciones cuando los pida.

Si usas el emulador, para probar el GPS hay que fijar una ubicación en *Extended controls → Location*. La biometría necesita tener una huella o PIN configurado en el emulador.

Las novedades se leen desde `contenido/actualizaciones.json` del repositorio, así que esa pantalla necesita internet. Si no hay conexión, muestra lo último que se descargó.

## Cómo trabajamos

- Código en GitHub, con commits por cada funcionalidad.
- Tareas y reparto del trabajo en Trello: [enlace al tablero]

## Cosas pendientes

- Los criterios de las rúbricas son de ejemplo, no los oficiales de cada entrega.
- Las fechas de entrega de cada etapa dependen del calendario de la escuela.
