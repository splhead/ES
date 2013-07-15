package br.com.AD7.silasladislau;


public class Trimestre {
	private long id;
	private String titulo;
	private int ordem_trimestre;
	private int ano;
	private String capa;
	
	public Trimestre(String titulo, int ordem_trimestre, int ano, String capa){
		super();
		this.titulo = titulo;
		this.ordem_trimestre = ordem_trimestre;
		this.ano = ano;
		this.capa = capa;
	}
	
	public Trimestre(long id,String titulo, int ordem_trimestre, int ano, String capa){
		super();
		this.id = id;
		this.titulo = titulo;
		this.ordem_trimestre = ordem_trimestre;
		this.ano = ano;
		this.capa = capa;
	}
	
	public long get_id() {
		return id;
	}

	public void set_id(long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public int getOrdemTrimestre() {
		return ordem_trimestre;
	}
	public void setOrdemTrimestre(int ordem_trimestre) {
		this.ordem_trimestre = ordem_trimestre;
	}
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
	public String getCapa() {
		return capa;
	}
	public void setCapa(String capa) {
		this.capa = capa;
	}
	public String toString(){		
		return getOrdemTrimestre()+ "º Trimestre Titulo: " + getTitulo() + " Ano: " +
		getAno();
	}
}
