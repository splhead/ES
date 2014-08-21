package br.com.AD7.silasladislau;

public class Licao {
	private long id;
	private String data;
	private String titulo;
	private int trimestreId;
	private byte[] capa;
	
	public Licao(long id, String data, String titulo, int trimestreId, byte[] capa) {
		super();
		this.id = id;
		this.data = data;
		this.titulo = titulo;
		this.trimestreId = trimestreId;
		this.capa = capa;
	}
	
	public Licao(String data, String titulo, int trimestreId, byte[] capa) {
		super();
		this.data = data;
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

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public int getTrimestreId() {
		return trimestreId;
	}

	public void setTrimestreId(int trimestreId) {
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
