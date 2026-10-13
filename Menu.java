/**
 * Kelas Menu — merepresentasikan satu item menu restoran.
 *
 * Perubahan dari versi sebelumnya:
 *   - Atribut harga diubah dari int ke long agar konsisten dengan
 *     kalkulasi di kelas Main dan menghindari perbedaan tipe data.
 *
 * Menunjukkan indikator 1:
 *   Penggunaan tipe data (String, long), variabel, identifier,
 *   keyword (class, private, public, this, return) dalam implementasi
 *   class, object, dan method.
 */
public class Menu {

    // Atribut objek — private agar hanya dapat diakses melalui getter
    private String nama;      // Nama item menu, contoh: "Nasi Goreng"
    private long   harga;     // Harga satuan dalam rupiah (diubah ke long)
    private String kategori;  // "Makanan" atau "Minuman"

    /**
     * Konstruktor — menginisialisasi objek Menu dengan ketiga atribut.
     *
     * @param nama      Nama item menu
     * @param harga     Harga satuan dalam rupiah
     * @param kategori  Kategori: "Makanan" atau "Minuman"
     */
    public Menu(String nama, long harga, String kategori) {
        this.nama     = nama;
        this.harga    = harga;
        this.kategori = kategori;
    }

    /** Mengembalikan nama menu. */
    public String getNama() {
        return nama;
    }

    /** Mengembalikan harga satuan menu dalam rupiah. */
    public long getHarga() {
        return harga;
    }

    /** Mengembalikan kategori menu ("Makanan" atau "Minuman"). */
    public String getKategori() {
        return kategori;
    }
}
