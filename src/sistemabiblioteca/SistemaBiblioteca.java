package sistemabiblioteca;

import java.io.*;
import java.util.Scanner;
import java.util.InputMismatchException; // Importado para validaciones avanzadas

// Clase base para estructurar los datos de los libros
class Libro {
    String codigo, titulo, autor, estado;

    public Libro(String codigo, String titulo, String autor, String estado) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.estado = estado; 
    }
}

// Clase base para estructurar el registro de prestamos
class Prestamo {
    String id, codLibro, estudiante, fechaPrestamo, fechaDevolucion;

    public Prestamo(String id, String codLibro, String estudiante, String fechaPrestamo, String fechaDevolucion) {
        this.id = id;
        this.codLibro = codLibro;
        this.estudiante = estudiante;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion; 
    }
}

public class SistemaBiblioteca {
    // Declaracion de arreglos como exige la rúbrica
    static Libro[] libros = new Libro[100];
    static Prestamo[] prestamos = new Prestamo[100];
    static int totalLibros = 0;
    static int totalPrestamos = 0;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        cargarDatos(); // Carga de persistencia al iniciar el sistema

        int opcion = -1;
        do {
            System.out.println("\n=========================================");
            System.out.println("   SISTEMA DE GESTION DE BIBLIOTECA");
            System.out.println("=========================================");
            System.out.println("1. Registrar nuevo libro");
            System.out.println("2. Registrar prestamo");
            System.out.println("3. Registrar devolucion");
            System.out.println("4. Mostrar inventario completo");
            System.out.println("5. Reporte: Libros prestados actualmente"); // Reporte adicional
            System.out.println("6. Guardar y Salir");
            System.out.print("Seleccione una opcion: ");
            
            // VALIDACIÓN AVANZADA: Prevenir colapso por entrada de texto en lugar de números
            try {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar el buffer
            } catch (InputMismatchException e) {
                System.out.println("\n[ERROR CRITICO] Debe ingresar un número entero valido.");
                scanner.nextLine(); // Limpiar la entrada incorrecta
                continue; // Reiniciar el ciclo
            }

            switch (opcion) {
                case 1: registrarLibro(); break;
                case 2: registrarPrestamo(); break;
                case 3: registrarDevolucion(); break;
                case 4: mostrarLibros(); break;
                case 5: mostrarReportePrestados(); break;
                case 6: guardarDatos(); break;
                default: System.out.println("\n[AVISO] Opcion invalida. Intente de nuevo con un numero del 1 al 6.");
            }
        } while (opcion != 6);
    }

    static void registrarLibro() {
        if (totalLibros >= 100) {
            System.out.println("\n[AVISO] Limite de capacidad del inventario alcanzado.");
            return;
        }
        System.out.println("\n--- REGISTRO DE LIBRO ---");
        System.out.print("Ingrese codigo del libro (Ej. LIB01): ");
        String codigo = scanner.nextLine();
        
        // Validación para no duplicar códigos
        if (buscarLibro(codigo) != -1) {
            System.out.println("[ERROR] Ya existe un libro con ese codigo.");
            return;
        }

        System.out.print("Ingrese título: ");
        String titulo = scanner.nextLine();
        System.out.print("Ingrese autor: ");
        String autor = scanner.nextLine();

        libros[totalLibros] = new Libro(codigo, titulo, autor, "disponible");
        totalLibros++;
        System.out.println("\n[ÉXITO] ¡Libro registrado correctamente!");
    }

    static void registrarPrestamo() {
        System.out.println("\n--- REGISTRO DE PRESTAMO ---");
        System.out.print("Ingrese el codigo del libro a prestar: ");
        String codigo = scanner.nextLine();

        int indexLibro = buscarLibro(codigo);
        if (indexLibro == -1) {
            System.out.println("[ERROR] El libro no existe en el inventario.");
            return;
        }

        if (libros[indexLibro].estado.equals("prestado")) {
            System.out.println("[ERROR] El libro ya se encuentra prestado actualmente.");
            return;
        }

        System.out.print("Ingrese ID del prestamo: ");
        String id = scanner.nextLine();
        System.out.print("Ingrese nombre del estudiante: ");
        String estudiante = scanner.nextLine();
        System.out.print("Ingrese fecha de prestamo (DD/MM/AAAA): ");
        String fechaP = scanner.nextLine();

        // Actualización de estado en el arreglo de libros
        libros[indexLibro].estado = "prestado";
        
        // Inserción en el arreglo de préstamos
        prestamos[totalPrestamos] = new Prestamo(id, codigo, estudiante, fechaP, "Pendiente");
        totalPrestamos++;
        
        System.out.println("\n[EXITO] ¡Prestamo registrado y estado del libro actualizado!");
    }

    static void registrarDevolucion() {
        System.out.println("\n--- REGISTRO DE DEVOLUCIÓN ---");
        System.out.print("Ingrese el codigo del libro a devolver: ");
        String codigo = scanner.nextLine();

        int indexLibro = buscarLibro(codigo);
        if (indexLibro == -1) {
            System.out.println("[ERROR] El libro no existe en el sistema.");
            return;
        }

        if (libros[indexLibro].estado.equals("disponible")) {
            System.out.println("[AVISO] El libro ya consta como disponible en los registros.");
            return;
        }

        System.out.print("Ingrese fecha de devolucion (DD/MM/AAAA): ");
        String fechaD = scanner.nextLine();

        libros[indexLibro].estado = "disponible";

        for (int i = 0; i < totalPrestamos; i++) {
            if (prestamos[i].codLibro.equals(codigo) && prestamos[i].fechaDevolucion.equals("Pendiente")) {
                prestamos[i].fechaDevolucion = fechaD;
                break;
            }
        }
        System.out.println("\n[EXITO] ¡Devolucion procesada y libro nuevamente disponible!");
    }

    static void mostrarLibros() {
        System.out.println("\n--- INVENTARIO COMPLETO ---");
        if (totalLibros == 0) {
            System.out.println("No hay libros registrados aun.");
            return;
        }
        // Formato tabular básico
        System.out.printf("%-10s | %-25s | %-15s | %-15s\n", "CODIGO", "TITULO", "AUTOR", "ESTADO");
        System.out.println("-----------------------------------------------------------------------");
        for (int i = 0; i < totalLibros; i++) {
            System.out.printf("%-10s | %-25s | %-15s | %-15s\n", libros[i].codigo, libros[i].titulo, libros[i].autor, libros[i].estado);
        }
    }

    // FUNCIONALIDAD EXTRA: Reporte filtrado
    static void mostrarReportePrestados() {
        System.out.println("\n--- REPORTE: LIBROS PRESTADOS ---");
        boolean hayPrestados = false;
        System.out.printf("%-10s | %-25s | %-20s\n", "CODIGO", "TITULO", "ESTUDIANTE (ID)");
        System.out.println("---------------------------------------------------------------");
        for (int i = 0; i < totalPrestamos; i++) {
            if (prestamos[i].fechaDevolucion.equals("Pendiente")) {
                // Busca el título del libro correspondiente
                String titulo = "";
                int idx = buscarLibro(prestamos[i].codLibro);
                if (idx != -1) titulo = libros[idx].titulo;
                
                System.out.printf("%-10s | %-25s | %-20s\n", prestamos[i].codLibro, titulo, prestamos[i].estudiante + " (" + prestamos[i].id + ")");
                hayPrestados = true;
            }
        }
        if (!hayPrestados) {
            System.out.println("Actualmente no hay ningun libro en calidad de prestamo.");
        }
    }

    static int buscarLibro(String codigo) {
        for (int i = 0; i < totalLibros; i++) {
            if (libros[i].codigo.equalsIgnoreCase(codigo)) {
                return i; // Retorna el índice si lo encuentra
            }
        }
        return -1; // Retorna -1 si no existe
    }

    // MÉTODOS DE PERSISTENCIA MEDIANTE ESCRITURA Y LECTURA DE ARCHIVOS
    static void guardarDatos() {
        try {
            BufferedWriter bwLibros = new BufferedWriter(new FileWriter("libros.txt"));
            for (int i = 0; i < totalLibros; i++) {
                bwLibros.write(libros[i].codigo + "," + libros[i].titulo + "," + libros[i].autor + "," + libros[i].estado + "\n");
            }
            bwLibros.close();

            BufferedWriter bwPrestamos = new BufferedWriter(new FileWriter("prestamos.txt"));
            for (int i = 0; i < totalPrestamos; i++) {
                bwPrestamos.write(prestamos[i].id + "," + prestamos[i].codLibro + "," + prestamos[i].estudiante + "," + prestamos[i].fechaPrestamo + "," + prestamos[i].fechaDevolucion + "\n");
            }
            bwPrestamos.close();
            
            System.out.println("\n[SISTEMA] Datos guardados en archivos .txt exitosamente. ¡Hasta pronto!");
        } catch (IOException e) {
            System.out.println("\n[ERROR CRITICO] Fallo al guardar los archivos: " + e.getMessage());
        }
    }

    static void cargarDatos() {
        try {
            File fLibros = new File("libros.txt");
            if (fLibros.exists()) {
                BufferedReader brLibros = new BufferedReader(new FileReader(fLibros));
                String linea;
                while ((linea = brLibros.readLine()) != null) {
                    String[] datos = linea.split(",");
                    if(datos.length == 4) {
                        libros[totalLibros] = new Libro(datos[0], datos[1], datos[2], datos[3]);
                        totalLibros++;
                    }
                }
                brLibros.close();
            }

            File fPrestamos = new File("prestamos.txt");
            if (fPrestamos.exists()) {
                BufferedReader brPrestamos = new BufferedReader(new FileReader(fPrestamos));
                String linea;
                while ((linea = brPrestamos.readLine()) != null) {
                    String[] datos = linea.split(",");
                    if(datos.length == 5) {
                        prestamos[totalPrestamos] = new Prestamo(datos[0], datos[1], datos[2], datos[3], datos[4]);
                        totalPrestamos++;
                    }
                }
                brPrestamos.close();
            }
        } catch (IOException e) {
            System.out.println("\n[AVISO] No se encontraron registros previos. Se iniciara un inventario en blanco.");
        }
    }
}