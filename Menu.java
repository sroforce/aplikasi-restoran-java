/**
 * Kelas Menu — merepresentasikan satu item menu restoran.
 *
 * Menunjukkan indikator 1:
 *   - Penggunaan tipe data (String, int), variabel, identifier, keyword
 *     (class, private, public, this, return) dalam implementasi class dan object.
 */
public class Menu {

    // Atribut objek Menu
    private String nama;      // Nama item menu
    private int    harga;     // Harga satuan (dalam rupiah, tanpa desimal)
    private String kategori;  // "Makanan" atau "Minuman"

    /**
     * Konstruktor — menginisialisasi objek Menu dengan ketiga atribut.
     *
     * @param nama      Nama item menu
     * @param harga     Harga satuan dalam rupiah
     * @param kategori  Kategori: "Makanan" atau "Minuman"
     */
    public Menu(String nama, int harga, String kategori) {
        this.nama     = nama;
        this.harga    = harga;
        this.kategori = kategori;
    }

    // Getter untuk nama
    public String getNama() {
        return nama;
    }

    // Getter untuk harga
    public int getHarga() {
        return harga;
    }

    // Getter untuk kategori
    public String getKategori() {
        return kategori;
    }
  }
