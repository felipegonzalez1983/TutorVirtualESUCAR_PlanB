
# Tutor Virtual ESUCAR — Plan B

## Descripción

Tutor Virtual ESUCAR es un prototipo académico de
aplicación móvil Android destinado a apoyar la
organización del Trabajo de Título de estudiantes
de la Escuela de Suboficiales de Carabineros de Chile.

El proyecto fue desarrollado para la asignatura
Desarrollo de Aplicaciones Móviles (DSY1105)
de Duoc UC.

Esta versión corresponde a un desarrollo individual
de contingencia (Plan B), paralelo al proyecto
del equipo académico.

## Responsable

Felipe González // Vicente Valenzuela // Yanina Silva

## Tecnologías utilizadas

- Kotlin
- Android Studio
- Jetpack Compose
- Material Design 3
- DataStore Preferences
- Corrutinas de Kotlin
- Git y GitHub
- HTTP y JSON

## Funcionalidades implementadas

### Etapas metodológicas
- Listado de cinco etapas académicas.
- Consulta del detalle de cada etapa.
- Buscador de etapas.
- Guía interactiva de cinco pasos.
- Navegación entre pantallas.

### Preguntas frecuentes
- Preguntas organizadas por categorías.
- Búsqueda sin distinguir mayúsculas ni tildes.
- Respuestas expandibles.

### Seguimiento académico
- Estados: Por iniciar, En desarrollo,
  Enviado y Aprobado.
- Modificación manual de estados.
- Persistencia local con DataStore.
- Estados compartidos entre pantallas.

### Calendario académico
- Listado de hitos y actividades.
- Fechas de entrega editables.
- Ordenamiento cronológico.
- Marcado de actividades completadas.
- Avisos de vencimientos próximos.

### Notificaciones
- Notificación de prueba.
- Recordatorios programados para
  uno y tres días antes de la entrega.
- Activación individual de recordatorios.
- Reprogramación tras reinicio del dispositivo.

### Rúbricas
- Consulta de criterios por etapa.
- Contenido disponible sin conexión.
- Identificación de criterios orientativos.

### Novedades académicas
- Descarga de novedades desde GitHub.
- Lectura de contenido JSON.
- Copia local para acceso sin conexión.
- Visualización de la última sincronización.

### Accesibilidad
- Modo normal y alto contraste.
- Preferencia guardada en DataStore.
- Colores adaptados en las pantallas
  principales de la aplicación.

## Estructura del proyecto

- model: modelos de datos.
- data: repositorios y persistencia.
- navigation: navegación de pantallas.
- ui/screens: interfaces de usuario.
- ui/theme: colores y estilos.
- notifications: notificaciones y alarmas.

## Requisitos para ejecutar

- Android Studio.
- SDK de Android 37.
- Dispositivo o emulador Android
  compatible con API 24 o superior.

## Instalación

1. Clonar el repositorio.
2. Abrir el proyecto en Android Studio.
3. Sincronizar Gradle.
4. Seleccionar un dispositivo o emulador.
5. Ejecutar la aplicación.

Para recibir notificaciones en versiones
compatibles de Android, conceder el permiso
de notificaciones cuando sea solicitado.

## Pruebas funcionales

Se han realizado pruebas manuales de las
12 historias de usuario del prototipo,
incluyendo navegación, persistencia local,
recordatorios y alto contraste.

La revisión formal de los requisitos técnicos
de la evaluación se realiza por separado.

## Consideraciones

Este proyecto es un prototipo educativo.

Las etapas, fechas, rúbricas y novedades
de demostración no representan necesariamente
información oficial de ESUCAR.

Los estados académicos son registrados
manualmente por el usuario y no equivalen
a una aprobación institucional.
