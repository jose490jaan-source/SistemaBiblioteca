package sistemabiblioteca;

import java.io.*;
import java.util.Scanner;
import java.util.InputMismatchException; // Importado para validaciones avanzadas

// Clase base para estructurar los datos de los libros
class Libro {
    // Variables para almacenar las propiedades de cada libro
    String codigo, titulo, autor, estado;

    // Constructor: se ejecuta al crear un nuevo objeto Libro para inicializar sus valores
    public Libro(String codigo, String titulo, String autor, String estado) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.estado = estado; 
    }
}

// Clase base para estructurar el registro de prestamos
class Prestamo {
    // Variables para vincular un libro con un estudiante y sus fechas
    String id, codLibro, estudiante, fechaPrestamo, fechaDevolucion;

    // Constructor: inicializa los datos obligatorios al momento de hacer un préstamo
    public Prestamo(String id, String codLibro, String estudiante, String fechaPrestamo, String fechaDevolucion) {
        this.id = id;
        this.codLibro = codLibro;
        this.estudiante = estudiante;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion; 
    }
}

public class SistemaBiblioteca {
    // Declaracion de arreglos estáticos para almacenar hasta 100 registros en memoria
    static Libro[] libros = new Libro[100];
    static Prestamo[] prestamos = new Prestamo[100];
    
    // Variables contadoras para saber cuántos elementos reales hay en los arreglos
    static int totalLibros = 0;
    static int totalPrestamos = 0;
    
    // Objeto Scanner para capturar la entrada del usuario por teclado
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Al arrancar, intentamos cargar los datos guardados previamente en los .txt
        cargarDatos(); 

        int opcion = -1; // Variable para almacenar la decisión del usuario en el menú
        
        // Bucle do-while que mantiene el programa en ejecución hasta que el usuario elija salir (opción 6)
        do {
            System.out.println("\n=========================================");
            System.out.println("   SISTEMA DE GESTION DE BIBLIOTECA");
            System.out.println("=========================================");
            System.out.println("1. Registrar nuevo libro");
            System.out.println("2. Registrar prestamo");
            System.out.println("3. Registrar devolucion");
            System.out.println("4. Mostrar inventario completo");
            System.out.println("5. Reporte: Libros prestados actualmente"); 
            System.out.println("6. Guardar y Salir");
            System.out.print("Seleccione una opcion: ");
            
            // VALIDACIÓN AVANZADA: Bloque try-catch para evitar que el programa falle 
            // si el usuario teclea letras o símbolos en lugar de un número.
            try {
                opcion = scanner.nextInt(); // Intenta leer un número entero
                scanner.nextLine(); // Limpiar el buffer (el salto de línea sobrante)
            } catch (InputMismatchException e) {
                System.out.println("\n[ERROR CRITICO] Debe ingresar un numero entero valido.");
                scanner.nextLine(); // Limpiar la entrada incorrecta para que no se quede en un bucle infinito
                continue; // Vuelve a mostrar el menú inmediatamente
            }

            // Estructura switch para dirigir al usuario a la función correspondiente
            switch (opcion) {
                case 1: registrarLibro(); break;
                case 2: registrarPrestamo(); break;
                case 3: registrarDevolucion(); break;
                case 4: mostrarLibros(); break;
                case 5: mostrarReportePrestados(); break;
                case 6: guardarDatos(); break;
                default: System.out.println("\n[AVISO] Opcion invalida. Intente de nuevo con un numero del 1 al 6.");
            }
        } while (opcion != 6); // Condición de salida del programa
    }

    // Método para agregar un nuevo libro al inventario
    static void registrarLibro() {
        // Validamos que no se exceda el tamaño máximo del arreglo (100)
        if (totalLibros >= 100) {
            System.out.println("\n[AVISO] Limite de capacidad del inventario alcanzado.");
            return; // Salimos de la función si está lleno
        }
        
        System.out.println("\n--- REGISTRO DE LIBRO ---");
        System.out.print("Ingrese codigo del libro (Ej. LIB01): ");
        String codigo = scanner.nextLine();
        
        // Validación: usamos nuestra función buscarLibro para evitar registrar códigos duplicados
        if (buscarLibro(codigo) != -1) {
            System.out.println("[ERROR] Ya existe un libro con ese codigo.");
            return;
        }

        // Solicitamos el resto de los datos
        System.out.print("Ingrese titulo: ");
        String titulo = scanner.nextLine();
        System.out.print("Ingrese autor: ");
        String autor = scanner.nextLine();

        // Creamos un nuevo objeto Libro y lo guardamos en la posición libre del arreglo
        // Todo libro nuevo se registra por defecto con el estado "disponible"
        libros[totalLibros] = new Libro(codigo, titulo, autor, "disponible");
        totalLibros++; // Aumentamos el contador de libros registrados
        
        System.out.println("\n[EXITO] ¡Libro registrado correctamente!");
    }

    // Método para prestar un libro a un estudiante
    static void registrarPrestamo() {
        System.out.println("\n--- REGISTRO DE PRESTAMO ---");
        System.out.print("Ingrese el codigo del libro a prestar: ");
        String codigo = scanner.nextLine();

        // Buscamos si el libro existe y obtenemos su posición en el arreglo
        int indexLibro = buscarLibro(codigo);
        
        // Si buscarLibro devuelve -1, significa que no lo encontró
        if (indexLibro == -1) {
            System.out.println("[ERROR] El libro no existe en el inventario.");
            return;
        }

        // Validamos que el libro no esté prestado revisando su atributo 'estado'
        if (libros[indexLibro].estado.equals("prestado")) {
            System.out.println("[ERROR] El libro ya se encuentra prestado actualmente.");
            return;
        }

        // Solicitamos los datos del préstamo
        System.out.print("Ingrese ID del prestamo: ");
        String id = scanner.nextLine();
        System.out.print("Ingrese nombre del estudiante: ");
        String estudiante = scanner.nextLine();
        System.out.print("Ingrese fecha de prestamo (DD/MM/AAAA): ");
        String fechaP = scanner.nextLine();

        // ACTUALIZACIÓN DE ESTADO: El libro ya no está disponible
        libros[indexLibro].estado = "prestado";
        
        // Creamos un nuevo objeto Prestamo. La fecha de devolución es "Pendiente" al inicio.
        prestamos[totalPrestamos] = new Prestamo(id, codigo, estudiante, fechaP, "Pendiente");
        totalPrestamos++; // Aumentamos el contador de préstamos
        
        System.out.println("\n[EXITO] ¡Prestamo registrado y estado del libro actualizado!");
    }

    // Método para devolver un libro y registrar la fecha de retorno
    static void registrarDevolucion() {
        System.out.println("\n--- REGISTRO DE DEVOLUCION ---");
        System.out.print("Ingrese el codigo del libro a devolver: ");
        String codigo = scanner.nextLine();

        // Verificamos que el código ingresado pertenezca a un libro real
        int indexLibro = buscarLibro(codigo);
        if (indexLibro == -1) {
            System.out.println("[ERROR] El libro no existe en el sistema.");
            return;
        }

        // Si el libro ya dice "disponible", no tiene sentido devolverlo
        if (libros[indexLibro].estado.equals("disponible")) {
            System.out.println("[AVISO] El libro ya consta como disponible en los registros.");
            return;
        }

        System.out.print("Ingrese fecha de devolucion (DD/MM/AAAA): ");
        String fechaD = scanner.nextLine();

        // ACTUALIZACIÓN DE ESTADO: El libro vuelve a estar disponible para todos
        libros[indexLibro].estado = "disponible";

        // Buscamos en el arreglo de préstamos cuál es el registro exacto a actualizar
        for (int i = 0; i < totalPrestamos; i++) {
            // Buscamos que coincida el código del libro Y que aún tenga estado "Pendiente"
            if (prestamos[i].codLibro.equals(codigo) && prestamos[i].fechaDevolucion.equals("Pendiente")) {
                // Reemplazamos la palabra "Pendiente" por la fecha real de devolución
                prestamos[i].fechaDevolucion = fechaD;
                break; // Rompemos el ciclo porque ya encontramos y actualizamos el préstamo
            }
        }
        System.out.println("\n[EXITO] ¡Devolucion procesada y libro nuevamente disponible!");
    }

    // Método para mostrar todos los libros sin importar su estado
    static void mostrarLibros() {
        System.out.println("\n--- INVENTARIO COMPLETO ---");
        
        // Validamos si el arreglo está vacío
        if (totalLibros == 0) {
            System.out.println("No hay libros registrados aun.");
            return;
        }
        
        // Usamos System.out.printf para formatear la salida como una tabla alineada
        // %-10s significa: un String (s) alineado a la izquierda (-) en un espacio de 10 caracteres
        System.out.printf("%-10s | %-25s | %-15s | %-15s\n", "CODIGO", "TITULO", "AUTOR", "ESTADO");
        System.out.println("-----------------------------------------------------------------------");
        
        // Iteramos sobre todos los libros registrados usando la variable totalLibros como límite
        for (int i = 0; i < totalLibros; i++) {
            System.out.printf("%-10s | %-25s | %-15s | %-15s\n", libros[i].codigo, libros[i].titulo, libros[i].autor, libros[i].estado);
        }
    }

    // Método de reporte específico que filtra solo los libros prestados
    static void mostrarReportePrestados() {
        System.out.println("\n--- REPORTE: LIBROS PRESTADOS ---");
        boolean hayPrestados = false; // Bandera para saber si encontramos al menos uno
        
        // Encabezados de la tabla
        System.out.printf("%-10s | %-25s | %-20s\n", "CODIGO", "TITULO", "ESTUDIANTE (ID)");
        System.out.println("---------------------------------------------------------------");
        
        // Recorremos el arreglo de préstamos
        for (int i = 0; i < totalPrestamos; i++) {
            // Filtramos únicamente los registros cuya fecha de devolución dice "Pendiente"
            if (prestamos[i].fechaDevolucion.equals("Pendiente")) {
                
                // Buscamos el título del libro correspondiente usando su código
                String titulo = "";
                int idx = buscarLibro(prestamos[i].codLibro);
                if (idx != -1) titulo = libros[idx].titulo;
                
                // Imprimimos el registro formateado
                System.out.printf("%-10s | %-25s | %-20s\n", prestamos[i].codLibro, titulo, prestamos[i].estudiante + " (" + prestamos[i].id + ")");
                hayPrestados = true; // Cambiamos la bandera ya que encontramos resultados
            }
        }
        
        // Si terminamos de buscar y la bandera sigue falsa, avisamos al usuario
        if (!hayPrestados) {
            System.out.println("Actualmente no hay ningun libro en calidad de prestamo.");
        }
    }

    // Función auxiliar que recorre el arreglo buscando un código de libro específico
    static int buscarLibro(String codigo) {
        for (int i = 0; i < totalLibros; i++) {
            // equalsIgnoreCase ignora si el usuario escribió en mayúsculas o minúsculas
            if (libros[i].codigo.equalsIgnoreCase(codigo)) {
                return i; // Retorna la posición (índice) exacta donde se encontró el libro
            }
        }
        return -1; // Retorna -1 (índice inválido) si termina de buscar y no encuentra nada
    }

    // =====================================================================
    // MÉTODOS DE PERSISTENCIA MEDIANTE ESCRITURA Y LECTURA DE ARCHIVOS
    // =====================================================================
    
    // Método para guardar los arreglos en archivos .txt
    static void guardarDatos() {
        try {
            // BufferedWriter permite escribir texto en un archivo de forma eficiente
            // FileWriter abre o crea el archivo "libros.txt" en modo escritura (sobreescribe)
            BufferedWriter bwLibros = new BufferedWriter(new FileWriter("libros.txt"));
            
            // Recorremos el arreglo y escribimos cada propiedad separada por una coma (Formato CSV)
            for (int i = 0; i < totalLibros; i++) {
                bwLibros.write(libros[i].codigo + "," + libros[i].titulo + "," + libros[i].autor + "," + libros[i].estado + "\n");
            }
            bwLibros.close(); // Siempre se debe cerrar el archivo para liberar memoria y evitar bloqueos

            // Repetimos el mismo proceso para los préstamos
            BufferedWriter bwPrestamos = new BufferedWriter(new FileWriter("prestamos.txt"));
            for (int i = 0; i < totalPrestamos; i++) {
                bwPrestamos.write(prestamos[i].id + "," + prestamos[i].codLibro + "," + prestamos[i].estudiante + "," + prestamos[i].fechaPrestamo + "," + prestamos[i].fechaDevolucion + "\n");
            }
            bwPrestamos.close();
            
            System.out.println("\n[SISTEMA] Datos guardados en archivos .txt exitosamente. ¡Hasta pronto!");
        } catch (IOException e) {
            // Se ejecuta si hay problemas de permisos o disco lleno
            System.out.println("\n[ERROR CRITICO] Fallo al guardar los archivos: " + e.getMessage());
        }
    }

    // Método para recuperar los datos desde los archivos .txt hacia la memoria (arreglos)
    static void cargarDatos() {
        try {
            // Creamos un objeto File para verificar la existencia de "libros.txt"
            File fLibros = new File("libros.txt");
            
            // Si el archivo existe, procedemos a leerlo
            if (fLibros.exists()) {
                // BufferedReader lee el texto línea por línea
                BufferedReader brLibros = new BufferedReader(new FileReader(fLibros));
                String linea;
                
                // Mientras el archivo tenga líneas con texto, seguimos leyendo
                while ((linea = brLibros.readLine()) != null) {
                    // Dividimos la línea leída cada vez que encontramos una coma
                    String[] datos = linea.split(",");
                    
                    // Si al dividir se generan 4 datos (código, título, autor, estado), es un registro válido
                    if(datos.length == 4) {
                        // Creamos un objeto Libro nuevo y lo agregamos al arreglo
                        libros[totalLibros] = new Libro(datos[0], datos[1], datos[2], datos[3]);
                        totalLibros++; // Aumentamos el contador
                    }
                }
                brLibros.close(); // Cerramos el archivo de lectura
            }

            // Repetimos el proceso lógico para cargar "prestamos.txt"
            File fPrestamos = new File("prestamos.txt");
            if (fPrestamos.exists()) {
                BufferedReader brPrestamos = new BufferedReader(new FileReader(fPrestamos));
                String linea;
                while ((linea = brPrestamos.readLine()) != null) {
                    String[] datos = linea.split(",");
                    // Un préstamo válido debe tener 5 datos
                    if(datos.length == 5) {
                        prestamos[totalPrestamos] = new Prestamo(datos[0], datos[1], datos[2], datos[3], datos[4]);
                        totalPrestamos++;
                    }
                }
                brPrestamos.close();
            }
        } catch (IOException e) {
            // Se ejecuta si ocurre un error inesperado al leer (ej. archivo corrupto)
            System.out.println("\n[AVISO] No se encontraron registros previos. Se iniciara un inventario en blanco.");
        }
    }
}