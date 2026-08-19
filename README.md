# Mi Formación CTMA

## Descripción del proyecto

Mi Formación CTMA es una aplicación móvil desarrollada para Android con Kotlin y Jetpack Compose. Su propósito es ayudar a los aprendices del SENA a organizar actividades, compromisos y evidencias de su proceso formativo en un solo lugar. La aplicación permitirá llevar un mejor control de las tareas asignadas, consultar fechas importantes y facilitar el seguimiento del avance académico de manera sencilla y organizada.

---

# Problema

Actualmente los aprendices administran sus actividades, enlaces, evidencias y fechas en diferentes plataformas, lo que genera desorganización, pérdida de información y dificultad para hacer seguimiento a su proceso de formación. Mi Formación CTMA busca centralizar esta información en una sola aplicación móvil para mejorar la organización, la productividad y la trazabilidad del aprendizaje.

---

# Usuarios

## 1. Aprendiz

**Necesidad:**
Consultar actividades, registrar evidencias y hacer seguimiento a su progreso académico.

## 2. Instructor

**Necesidad:**
Publicar actividades, establecer fechas de entrega y realizar seguimiento al avance de los aprendices.

---

# Historias de Usuario

## Historia 1

**Como** aprendiz,

**quiero** consultar mis actividades pendientes,

**para** organizar mejor mi tiempo y cumplir con las fechas establecidas.

### Criterio de aceptación

- La aplicación debe mostrar una lista con las actividades registradas.
- El aprendiz puede visualizar el nombre y la fecha de cada actividad.

---

## Historia 2

**Como** instructor,

**quiero** publicar nuevas actividades,

**para** que los aprendices puedan consultarlas desde la aplicación.

### Criterio de aceptación

- El instructor puede crear una nueva actividad.
- La actividad debe quedar disponible para ser consultada por los aprendices.

---

## Historia 3

**Como** aprendiz,

**quiero** registrar las evidencias de las actividades realizadas,

**para** llevar un control de mi progreso durante la formación.

### Criterio de aceptación

- El aprendiz puede marcar una actividad como completada.
- La aplicación debe reflejar el cambio de estado de la actividad.

---

# Tecnologías utilizadas

- Kotlin
- Android Studio
- Jetpack Compose
- Material Design 3
- Navigation Compose
- ViewModel & LiveData/State
- Gradle (Kotlin DSL)
- Git & GitHub

---

# Evolución del Proyecto

## Semana 1: Configuración Inicial
- Configuración del entorno Android y Jetpack Compose.
- Pantalla inicial básica y repositorio Git.

## Semana 2: Modelo de Datos y Reglas
- Implementación del modelo `ActividadFormativa` y enum `Prioridad`.
- Desarrollo de reglas de negocio (búsqueda, validación básica, promedios).

## Semana 3: Interfaz de Usuario (UI)
- Diseño de tarjetas (`ActividadCard`) y listas dinámicas.
- Aplicación de Material Design 3 y principios de accesibilidad.

## Semana 4: Estado, Formularios y Navegación
- **Arquitectura UDF (Unidirectional Data Flow)**: Implementación de `ViewModel`, `UiState` y eventos para un flujo de datos predecible.
- **Navegación**: Integración de `Navigation Compose` con rutas para Listado, Creación (Formulario) y Detalle de actividad.
- **Formularios con Validación**: 
    - Título obligatorio (3-80 caracteres).
    - Descripción opcional (máx. 240 caracteres).
    - Progreso validado (0-100).
    - Lógica de fechas (fecha de inicio no anterior a hoy).
- **Gestión de Estado**: Uso de `ViewModel` para mantener los datos durante cambios de configuración (rotación) y navegación.
- **Interactividad**: Protección contra doble toque en botones de acción y transiciones fluidas entre pantallas.

---

# Estado actual
✅ **Semana 4 completada.** La aplicación es funcional y permite el flujo completo de gestión de actividades formativas.