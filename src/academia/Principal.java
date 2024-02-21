package academia;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.matisse.MtDatabase;
import com.matisse.MtException;
import com.matisse.MtObjectIterator;

public class Principal {

	public static void main(String[] args) {
		String hostname = "localhost";
		String dbname = "academia";

		// crearObjetos(hostname, dbname);
		// eliminarObjetos(hostname,dbname);
		// modificarObjetos(hostname, dbname, "12345678W", "505505505");
		//consultarObjetos(hostname, dbname);
	}

	private static void crearObjetos(String hostname, String dbname) {
		try {
			/* Abre la base de datos con el hostname (localhost) y el nombre de la base de
			 * datos dbname (academia). */
			MtDatabase db = new MtDatabase(hostname, dbname);
			db.open();
			db.startTransaction();
			System.out.println("Conectado a la base de datos " + db.getName() + " de Matisse.");

			// Crear dos objetos Profesores
			Profesores profesor1 = new Profesores(db);
			profesor1.setNombre("Antonio");
			profesor1.setApellidos("García");
			profesor1.setTelefono("654654654");
			profesor1.setDni("12345678W");

			Profesores profesor2 = new Profesores(db);
			profesor2.setNombre("Julia");
			profesor2.setApellidos("Gomez");
			profesor2.setTelefono("678678678");
			profesor2.setDni("87654321R");

			// Crea dos objetos Asignaturas
			Asignaturas asignatura1 = new Asignaturas(db);
			asignatura1.setNombre("Programación Multimedia y Dispositivos Móviles");
			asignatura1.setAula(1);
			asignatura1.setDuracion(2.5f);
			asignatura1.setHoraInicio("16.30");
			asignatura1.setDiaSemana("lunes");

			Asignaturas asignatura2 = new Asignaturas(db);
			asignatura2.setNombre("Acceso a Datos");
			asignatura2.setAula(2);
			asignatura2.setDuracion(1.5f);
			asignatura2.setHoraInicio("17.00");
			asignatura2.setDiaSemana("martes");

			// Crea un array de Clases para guardar las asignaturas y hacer las relaciones
			Clases clases1[] = new Clases[2];
			clases1[0] = asignatura1;
			clases1[1] = asignatura2;

			// Guarda las relaciones del profesor con la clase que imparte.
			profesor1.setImparten(clases1);
			// Ejecuta un commit para materializar los cambios en la base de datos.
			db.commit();
			// Cierra la base de datos.
			db.close();
			System.out.println("\nObjetos creados correctamente en la base de datos.");
		} catch (MtException mte) {
			System.out.println("MtException : " + mte.getMessage());
		}

	}

	private static void eliminarObjetos(String hostname, String dbname) {
		try {
			/* Abre la base de datos con el hostname (localhost) y el nombre de la base de
			 * datos dbname (academia). */
			MtDatabase db = new MtDatabase(hostname, dbname);
			db.open();
			db.startTransaction();
			System.out.println("Conectado a la base de datos " + db.getName() + " de Matisse.");
			/* El método getInstanceNumber(db) cuenta el número de objetos del tipo de la
			 * clase con la que lo llamemos que en este caso es Clases */
			System.out.println("\n" + Clases.getInstanceNumber(db) + " objetos de tipo Clases tenemos en la DB.");

			// Crea un Iterador (propio de Java)
			MtObjectIterator<Clases> iter = Clases.<Clases>instanceIterator(db);
			System.out.println("Borro dos objetos de tipo Clases");

			while (iter.hasNext()) {
				Clases[] clases = iter.next(2);
				System.out.println("Borrando " + clases.length + " objetos de tipo Clases.");
				for (int i = 0; i < clases.length; i++) {
					// borra el objeto
					clases[i].deepRemove();
				}
				// sólo borra 2 objetos y sale del bucle.
				break;
			}
			System.out.println("Después de borrar los objetos Clases tenemos " + Clases.getInstanceNumber(db)
					+ " objetos de tipo Clases en la BD " + db.getName());
			System.out.println("Objetos borrados correctamente en la base de datos.");

			iter.close();
			db.commit();
			db.close();
		} catch (MtException mte) {
			System.out.println("MtException : " + mte.getMessage());
		}
	}

	// método para modificar un objeto de tipo Profesores pasándole su dni y un nuevo número de teléfono
	private static void modificarObjetos(String hostname, String dbname, String dniProfesor, String nuevoNumeroTel) {
		int nProfesores = 0;
		try {
			MtDatabase db = new MtDatabase(hostname, dbname);
			db.open();
			db.startTransaction();
			System.out.println("Conectado a la base de datos " + db.getName() + " de Matisse.");
			/*
			 * El método getInstanceNumber(db) cuenta el número de objetos del tipo de la
			 * clase con la que lo llamemos que en este caso es Profesores.
			 */
			System.out
					.println("\n" + Profesores.getInstanceNumber(db) + " objetos de tipo Profesores tenemos en la DB.");
			nProfesores = (int) Profesores.getInstanceNumber(db);
			// Crea un Iterador (propio de Java)
			MtObjectIterator<Profesores> iter = Profesores.<Profesores>instanceIterator(db);
			System.out.println("\nRecorro el iterador de uno en uno y cambio cuando encuentro 'dni' = " + dniProfesor);

			while (iter.hasNext()) {
				Profesores[] profesores = iter.next(nProfesores);
				for (int i = 0; i < profesores.length; i++) {
					if (profesores[i].getDni().compareTo(dniProfesor) == 0) {
						profesores[i].setTelefono(nuevoNumeroTel);
						System.out.println("Objeto modificado!");
					}
				}
			}
			iter.close();
			// Materializa los cambios y cierra la BD
			db.commit();
			db.close();
			System.out.println("\nLa modificación del objeto, finalizada correctamente.");
		} catch (MtException mte) {
			System.out.println("MtException : " + mte.getMessage());
		}
	}

	private static void consultarObjetos(String hostname, String dbname) {
		MtDatabase dbcon = new MtDatabase(hostname, dbname);
		// Abre una conexión a la base de datos
		dbcon.open();
		try {
			// Crea una instancia de Statement
			Statement stmt = dbcon.createStatement();
			/* Asigna una consulta OQL. Esta consulta lo que hace es utilizar REF() para
			 * obtener el objeto directamente. */
			String commandText = "SELECT REF(p) from academia.Profesores p;";

			/* Ejecuta la consulta y obtiene un ResultSet que contendrá las referencias a
			 * los objetos que en este caso serán de tipo Profesores. */
			ResultSet rset = stmt.executeQuery(commandText);
			
			/*Creamos una referencia a un objeto de tipo Profesores donde almacenaremos los
			 * objetos devueltos en el ResultSet. */
			Profesores profesor1;

			// Recorremos el ResultSet.
			while (rset.next()) {
				/* Con el método getObject() recuperamos cada objeto del ResultSet y lo
				 * almacenamos en profesor1. */
				profesor1 = (Profesores)rset.getObject(1);

				/* Una vez el objeto es referenciado en profesor1, ya se pueden recuperar de él los
				 * valores de sus atributos. */
				System.out.println("Los valores de los atributos del objeto de tipo Profesores son: " + profesor1.getNombre() + " "
						+ profesor1.getApellidos() + " " + profesor1.getDni() + " " + profesor1.getTelefono() + ".");
			}
			/* Cierra las conexiones. Solamente debemos cerrar el ResultSet y el Statement,
			 * no el MtDatabase porque lanza una excepción de que no conoce la fuente. */
			rset.close();
			stmt.close();
			// dbcon.close();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}
	}
}
