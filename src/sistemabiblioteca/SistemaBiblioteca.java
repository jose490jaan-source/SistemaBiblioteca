package sistemabiblioteca;

import java.io.*;
import java.util.Scanner;

// Clase para manejar los datos del libro
class Libro {
    String codigo, titulo, autor, estado;

    public Libro(String codigo, String titulo, String autor, String estado) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.estado = estado; // 'disponible' o 'prestado'
    }
}

// Clase para manejar los datos del préstamo
class Prestamo {
    String id, codLibro, estudiante, fechaPrestamo, fechaDevolucion;

    public Prestamo(String id, String codLibro, String estudiante, String fechaPrestamo, String fechaDevolucion) {
        this.id = id;
        this.codLibro = codLibro;
        this.estudiante = estudiante;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion; // Puede estar vacía inicialmente
    }
}

public class SistemaBiblioteca {
    // Arreglos solicitados en la rúbrica
    static Libro[] libros = new Libro[100];
    static Prestamo[] prestamos = new Prestamo[100];
    static int totalLibros = 0;
    static int totalPrestamos = 0;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        cargarDatos(); // Carga la información de los .txt al iniciar

        int opcion;
        do {
            System.out.println("\n--- SISTEMA DE GESTION DE BIBLIOTECA ---");
            System.out.println("1. Registrar nuevo libro");
            System.out.println("2. Registrar prestamo");
            System.out.println("3. Registrar devolucion");
            System.out.println("4. Mostrar libros registrados");
            System.out.println("5. Guardar y Salir");
            System.out.print("Seleccione una opcion: ");
            
            while (!scanner.hasNextInt()) {
                System.out.println("Por favor ingrese un numero valido.");
                scanner.next();
            }
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            switch (opcion) {
                case 1: registrarLibro(); break;
                case 2: registrarPrestamo(); break;
                case 3: registrarDevolucion(); break;
                case 4: mostrarLibros(); break;
                case 5: guardarDatos(); break;
                default: System.out.println("Opcion invalida. Intente de nuevo.");
            }
        } while (opcion != 5);
    }

    static void registrarLibro() {
        if (totalLibros >= 100) {
            System.out.println("Limite de libros alcanzado.");
            return;
        }
        System.out.print("Ingrese codigo del libro: ");
        String codigo = scanner.nextLine();
        System.out.print("Ingrese titulo: ");
        String titulo = scanner.nextLine();
        System.out.print("Ingrese autor: ");
        String autor = scanner.nextLine();

        libros[totalLibros] = new Libro(codigo, titulo, autor, "disponible");
        totalLibros++;
        System.out.println("Libro registrado con exito");
    }

    static void registrarPrestamo() {
        System.out.print("Ingrese el codigo del libro a prestar: ");
        String codigo = scanner.nextLine();

        int indexLibro = buscarLibro(codigo);
        if (indexLibro == -1) {
            System.out.println("Error: El libro no existe.");
            return;
        }

        if (libros[indexLibro].estado.equals("prestado")) {
            System.out.println("Error: El libro ya se encuentra prestado.");
            return;
        }

        System.out.print("Ingrese ID del prestamo: ");
        String id = scanner.nextLine();
        System.out.print("Ingrese nombre del estudiante: ");
        String estudiante = scanner.nextLine();
        System.out.print("Ingrese fecha de prestamo (DD/MM/AAAA): ");
        String fechaP = scanner.nextLine();

        // Cambiar estado del libro
        libros[indexLibro].estado = "prestado";
        
        // Registrar préstamo
        prestamos[totalPrestamos] = new Prestamo(id, codigo, estudiante, fechaP, "Pendiente");
        totalPrestamos++;
        
        System.out.println("Prestamo registrado con exito");
    }

    static void registrarDevolucion() {
        System.out.print("Ingrese el codigo del libro a devolver: ");
        String codigo = scanner.nextLine();

        int indexLibro = buscarLibro(codigo);
        if (indexLibro == -1) {
            System.out.println("Error: El libro no existe.");
            return;
        }

        if (libros[indexLibro].estado.equals("disponible")) {
            System.out.println("El libro ya consta como disponible en el sistema.");
            return;
        }

        System.out.print("Ingrese fecha de devolución (DD/MM/AAAA): ");
        String fechaD = scanner.nextLine();

        // Actualizar el estado del libro
        libros[indexLibro].estado = "disponible";

        // Buscar el préstamo correspondiente y actualizar la fecha de devolución
        for (int i = 0; i < totalPrestamos; i++) {
            if (prestamos[i].codLibro.equals(codigo) && prestamos[i].fechaDevolucion.equals("Pendiente")) {
                prestamos[i].fechaDevolucion = fechaD;
                break;
            }
        }
        System.out.println("Devolucion registrada con exito");
    }

    static void mostrarLibros() {
        System.out.println("\n--- LISTADO DE LIBROS ---");
        for (int i = 0; i < totalLibros; i++) {
            System.out.println(libros[i].codigo + " | " + libros[i].titulo + " | " + libros[i].estado);
        }
    }

    // Método auxiliar para encontrar la posición de un libro en el arreglo
    static int buscarLibro(String codigo) {
        for (int i = 0; i < totalLibros; i++) {
            if (libros[i].codigo.equals(codigo)) {
                return i;
            }
        }
        return -1; // No encontrado
    }

    // --- PERSISTENCIA DE DATOS ---
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
            
            System.out.println("Datos guardados en archivos .txt exitosamente. ¡Hasta pronto!");
        } catch (IOException e) {
            System.out.println("Error al guardar los archivos: " + e.getMessage());
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
            System.out.println("No se pudieron cargar los datos previos o los archivos no existen aún.");
        }
    }
}