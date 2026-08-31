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
- Gradle
- Git
- GitHub

---

# Estado del proyecto

🚧 Proyecto en desarrollo - Semana 3

Actualmente la aplicación cuenta con:

- Configuración del entorno Android.
- Proyecto creado con Jetpack Compose.
- Pantalla inicial adaptable.
- Persistencia en memoria (simulada).
- Arquitectura de paquetes organizada (ui/components, ui/screens, ui/theme).
- Documentación del proyecto actualizada.

# Semana 2

## Cambios realizados

- Se creó el paquete model.
- Se implementó la clase ActividadFormativa.
- Se creó el enum Prioridad.
- Se desarrollaron las reglas de negocio.
- Se implementó la búsqueda de actividades.
- Se calcula el promedio de progreso.
- Se muestran datos calculados en la pantalla inicial.

# Semana 3

## Cambios realizados

### 1. Interfaz de Usuario con Material 3
- **Tema y Estilos:** Centralización de colores, tipografías y formas en el paquete `ui/theme`.
- **Uso de Material 3:** Implementación de `ColorScheme`, `Typography` y `Shapes` para evitar valores arbitrarios en el código.

### 2. Componentes Reutilizables
- **EncabezadoFormacion:** Componente parametrizado que muestra el saludo al aprendiz y un resumen del estado de sus actividades. Incluye previsualizaciones para diferentes escalas de fuente y anchos de pantalla.
- **TarjetaActividad:** Componente sin estado (stateless) para mostrar detalles de una actividad (título, fecha, progreso y estado textual).
- **Accesibilidad:** Uso de `contentDescription` y modificadores semánticos para mejorar la experiencia con lectores de pantalla.

### 3. Pantalla Principal y Layout Adaptable
- **PantallaActividades:** Implementación de la estructura base usando `Scaffold` con un Floating Action Button.
- **Diseño Adaptable:** Uso de `BoxWithConstraints` para alternar entre una `LazyColumn` (pantallas compactas < 600dp) y una `LazyVerticalGrid` de 2 columnas (pantallas amplias >= 600dp).
- **Gestión de Estados:** Manejo visual de la lista vacía de actividades.
- **Optimización de Listas:** Uso de claves estables (`key`) en las colecciones para mejorar el rendimiento.
