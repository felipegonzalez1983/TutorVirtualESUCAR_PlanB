# Tutor Virtual ESUCAR — Plan B

**Aplicación móvil Android para el seguimiento académico, metodológico y documental de trabajos de título.**

Proyecto educativo desarrollado para la asignatura **Desarrollo de Aplicaciones Móviles (DSY1105)** de DUOC UC, utilizando Kotlin, Jetpack Compose y arquitectura MVVM.

> **Aviso institucional:** Tutor Virtual ESUCAR es un prototipo académico de demostración. No constituye una aplicación oficial ni autorizada por Carabineros de Chile. Los contenidos de ejemplo no representan necesariamente información institucional vigente.

## 1. Descripción del proyecto

Tutor Virtual ESUCAR busca apoyar la organización del trabajo de título de estudiantes de la Escuela de Suboficiales de Carabineros de Chile.

La aplicación permite consultar etapas metodológicas, gestionar entregables, organizar actividades académicas y registrar evidencias mediante documentos PDF y fotografías.

### Origen del Plan B

El proyecto comenzó como una versión individual de contingencia, desarrollada paralelamente al proyecto del equipo académico.

Esta versión evolucionó con funcionalidades de persistencia local, gestión documental e integración de recursos nativos de Android.

Actualmente se utiliza como base de desarrollo colaborativo para integrar mejoras de diseño y experiencia de usuario.

## 2. Equipo de desarrollo

| Integrante | Área de trabajo |
|---|---|
| Felipe González | Desarrollo Android, arquitectura, persistencia e integración |
| Yanina Silva | Colaboración y modernización visual |
| Vicente Valenzuela | Colaboración, pruebas y documentación |

Las responsabilidades podrán actualizarse conforme avance el proyecto.

## 3. Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje principal |
| Android Studio | Entorno de desarrollo |
| Jetpack Compose | Construcción de interfaces |
| Material Design 3 | Componentes visuales |
| MVVM | Separación de responsabilidades |
| Room / SQLite | Almacenamiento local |
| DataStore Preferences | Preferencias y persistencia de sesión |
| Kotlin Coroutines / Flow | Operaciones asíncronas y datos reactivos |
| MediaStore | Almacenamiento de fotografías |
| Android Activity Result API | Cámara y selección de documentos |
| HTTP / JSON | Consulta de novedades académicas |
| Git / GitHub | Control de versiones y colaboración |

## 4. Funcionalidades implementadas

### 4.1. Etapas metodológicas

- Listado de cinco etapas académicas.
- Consulta del detalle de cada etapa.
- Buscador de etapas.
- Guía metodológica interactiva.
- Navegación entre pantallas.
- Actualización manual del estado de cada etapa.

### 4.2. Gestión de entregables

- Registro de entregables académicos.
- Edición de títulos, descripciones y fechas.
- Cambio de estados: Por iniciar, En desarrollo, Enviado y Aprobado.
- Eliminación de entregables con confirmación.
- Persistencia local mediante Room.
- Asociación de entregables con etapas metodológicas.

### 4.3. Evidencias PDF

- Selección de documentos mediante el selector nativo de Android.
- Vinculación