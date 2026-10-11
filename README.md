💻📖 Sistema de Gestión de Biblioteca Escolar

Aplicación de escritorio en Java (Swing + JDBC + MySQL) para gestionar libros, estudiantes,
préstamos y devoluciones. Evaluación final transversal, Desarrollo Orientado a Objetos II.

## Funcionalidades ⚡
- Autenticación con rol **bibliotecario** o **estudiante**.
- Libros: registrar, editar, eliminar, buscar y filtrar por categoría.
- Estudiantes: registrar (usuario y ficha) y consultar su historial.
- Préstamos: plazo de 15 a 30 días con vencimiento automático, devoluciones y detección de atrasos.
- Reportes: libros más prestados, libros en préstamo e historial por estudiante.
- El estudiante solo consulta el catálogo y sus propios préstamos.

## Arquitectura 🔨
MVC + DAO: `vista` (Swing) → `controlador` (validación, permisos, transacciones) →
`dao` (SQL con PreparedStatement) → MySQL. `model` contiene las entidades.
Patrones: DAO, MVC, Singleton (`SesionActual`), Factory (`FabricaVentanas`), Observer (eventos Swing).
Concurrencia: `SwingWorker` en la interfaz, descuento de stock atómico con transacciones y
`ConcurrenciaDemo` (10 hilos pidiendo el último ejemplar).

## Requisitos
JDK 17 o superior, MySQL 8, IntelliJ IDEA (proyecto Maven).

## Cómo ejecutarlo
1. Crear y poblar la base de datos `biblioteca` con los scripts SQL entregados por el profesor.
2. Copiar `.env.example` a `.env` y poner la contraseña de tu MySQL.
3. Abrir el proyecto en IntelliJ y ejecutar `cl.duoc.app.Main`.

## Usuarios de prueba 👤👤
- Bibliotecario: `antonia@correo.cl` / `clave123`
- Estudiante: `carlos@correo.cl` / `clave123`

## Decisiones de diseño 📄 ✏️
- El esquema del profesor no se modificó. `fecha_devolucion` guarda el vencimiento y, al devolver
  un libro, se actualiza con la fecha real junto con `devuelto = TRUE`.
- El stock son los ejemplares disponibles: baja al prestar y sube al devolver.
- Del RUT solo se valida el formato.
