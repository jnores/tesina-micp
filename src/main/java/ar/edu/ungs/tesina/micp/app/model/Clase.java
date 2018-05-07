package ar.edu.ungs.tesina.micp.app.model;

import java.security.InvalidParameterException;

import ar.edu.ungs.tesina.micp.Vertex;

public class Clase extends Vertex {

	public int mId;
	public String mNombre;
	public String mDocente;
	public String mDia;
	public double mHoraInicio;
	public double mHoraFin;
	public int mPabellon;

	public enum Tipo {
		FCEN, ITC
	};

	public static Clase createClase(int id, String linea, Tipo tipo) {
		String[] campos;
		String nombre;
		String docente;
		String dia;
		double horaInicio;
		double horaFin;
		int pabellon;

		switch (tipo) {
		case FCEN:
			campos = linea.split("\\|");

			if (campos.length < 6)
				throw new RuntimeException("Error de formato! L�nea: " + linea);

			nombre = campos[0].trim();
			docente = campos[1].trim();
			dia = campos[2].trim();
			horaInicio = Double.parseDouble(campos[3].trim());
			horaFin = Double.parseDouble(campos[4].trim());
			pabellon = Integer.parseInt(campos[5].trim());
			break;

		case ITC:
			campos = linea.split(" ");

			if (campos.length < 4)
				throw new RuntimeException("Error de formato! L�nea: " + linea);

			nombre = campos[0].trim();
			docente = "";
			dia = campos[2].trim();
			horaInicio = Double.parseDouble(campos[3].trim());
			horaFin = horaInicio + 1;
			pabellon = 1;
			break;
		default:
			throw new InvalidParameterException("Tipo de estructura no implementada: " + tipo);
		}

		return new Clase(id, nombre, docente, dia, horaInicio, horaFin, pabellon);
	}

	public Clase(int id, String nombre, String docente, String dia, double horaInicio, double horaFin, int pabellon) {
		super("v" + String.format("%04d", id));
		mId = id;
		mNombre = nombre;
		mDocente = docente;
		mDia = dia;
		mHoraInicio = horaInicio;
		mHoraFin = horaFin;
		mPabellon = pabellon;

	}

	public static boolean seSuperponen(Clase primera, Clase segunda) {
		return primera.mDia.equals(segunda.mDia)
				&& !(primera.mHoraFin <= segunda.mHoraInicio || primera.mHoraInicio >= segunda.mHoraFin);
	}

	public static boolean igualMateria(Clase primera, Clase segunda) {
		String clavePrimera = primera.mNombre + "|" + primera.mDocente;
		String claveSegunda = segunda.mNombre + "|" + segunda.mDocente;

		return clavePrimera.equals(claveSegunda);
	}

	public String serialize() {
		return mNombre+"|"+mDocente+"|"+mDia+"|"+mHoraInicio+"|"+mHoraFin+"|"+mPabellon ;
	}

	public int getId() {
		return mId;
	}

	public String getNombre() {
		return mNombre;
	}

	public String getDocente() {
		return mDocente;
	}

	public String getDia() {
		return mDia;
	}

	public String getHoraInicio() {
		return toHora(mHoraInicio);
	}

	public String getHoraFin() {
		return toHora(mHoraFin);
	}

	private String toHora(double horaEncoded) {
		horaEncoded *= 24;
		int hora = (int)Math.round(horaEncoded);
		horaEncoded -= hora;
		int minutos =  (int)horaEncoded*60;
		
		return String.format("%02d:%02dhs", hora,minutos);
	}

	public int getPabellon() {
		return mPabellon;
	}
	
	
}
