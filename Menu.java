/**
 * Kelas Menu — merepresentasikan satu item menu restoran.
 *
 * Menunjukkan indikator 1:
 *   Penggunaan tipe data (String, long), variabel, identifier,
 *   keyword (class, private, public, this, return) dalam implementasi
 *   class, object, dan method.
 */
public class Menu {

    private String nama;
    private long   harga;
    private String kategori;

    public Menu(String nama, long harga, String kategori) {
        this.nama     = nama;
        this.harga    = harga;
        this.kategori = kategori;
    }

    public String getNama()     { return nama; }
    public long   getHarga()    { return harga; }
    public String getKategori() { return kategori; }
}
