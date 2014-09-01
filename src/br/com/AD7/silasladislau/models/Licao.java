package br.com.AD7.silasladislau.models;

public class Licao {
	private long id;
	private String data;
	private String titulo;
	private long trimestreId;
	private int  numero;
	private byte[] capa;
	
	public Licao(long id, String data, int numero, String titulo, long trimestreId, byte[] capa) {
		super();
		this.id = id;
		this.data = data;
		this.numero = numero;
		this.titulo = titulo;
		this.trimestreId = trimestreId;
		this.capa = capa;
	}
	
	public Licao(String data, int numero,String titulo, long trimestreId, byte[] capa) {
		super();
		this.data = data;
		this.numero = numero;
		this.titulo = titulo;
		this.trimestreId = trimestreId;
		this.capa = capa;
	}
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public long getTrimestreId() {
		return trimestreId;
	}

	public void setTrimestreId(long trimestreId) {
		this.trimestreId = trimestreId;
	}
	
	public byte[] getCapa() {
		return capa;
	}

	public void setCapa(byte[] capa) {
		this.capa = capa;
	}

	public String toString() {
		return this.getTitulo() + " " + this.getData() + " " + this.trimestreId;
	}
	
}
