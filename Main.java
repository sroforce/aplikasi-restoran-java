import java.util.Scanner;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Kelas utama — Aplikasi Restoran Sederhana "Nusantara Rasa".
 *
 * Fitur program:
 *   - Menampilkan 8 menu (4 makanan, 4 minuman) dari array objek Menu[]
 *   - Menerima maks 4 slot pesanan (format: Nama Menu = Jumlah)
 *   - Menghitung subtotal, promo B1G1, diskon 10%, pajak 10%, biaya pelayanan
 *   - Mencetak struk pembayaran lengkap
 *
 * Catatan: Program tidak menggunakan struktur perulangan (for / while /
 *   do-while / enhanced-for / forEach / stream / rekursi) sesuai arahan dosen.
 *   Semua iterasi digantikan dengan pemanggilan eksplisit per elemen.
 */
public class Main {

    // =========================================================
    //  DATA MENU — Array objek Menu (indikator 1 & 2)
    // =========================================================
    static Menu[] daftarMenu = new Menu[8];

    // =========================================================
    //  ASUMSI IMPLEMENTASI
    //  Dosen tidak menetapkan nama menu promo secara eksplisit.
    //  Dipilih "Es Teh Manis" sebagai menu promo beli-satu-gratis-satu.
    // =========================================================
    static final String NAMA_MENU_PROMO = "Es Teh Manis";

    // =========================================================
    //  KONSTANTA BIAYA
    // =========================================================
    static final int    BIAYA_PELAYANAN = 20000;  // Rp20.000 per transaksi
    static final double TARIF_PAJAK     = 0.10;   // 10%
    static final double TARIF_DISKON    = 0.10;   // 10%
    static final int    AMBANG_DISKON   = 100000; // Diskon aktif jika subtotalAwal > Rp100.000
    static final int    AMBANG_PROMO    = 50000;  // Promo aktif jika subtotalAwal > Rp50.000

    // =========================================================
    //  ASUMSI URUTAN PERHITUNGAN
    //  1. subtotalAwal  = jumlah semua (harga × qty)
    //  2. Uji ambang menggunakan subtotalAwal
    //  3. subtotalSetelahPromo  = subtotalAwal  - penguranganPromo
    //  4. subtotalSetelahDiskon = subtotalSetelahPromo - nominalDiskon
    //                            (diskon = 10% × subtotalSetelahPromo)
    //  5. dasarPajak   = subtotalSetelahDiskon
    //  6. nominalPajak = 10% × dasarPajak
    //  7. totalAkhir   = dasarPajak + nominalPajak + BIAYA_PELAYANAN
    // =========================================================


    // =========================================================
    //  MAIN
    // =========================================================
    public static void main(String[] args) {

        // Inisialisasi array objek Menu — 4 makanan dan 4 minuman
        daftarMenu[0] = new Menu("Nasi Goreng",  25000, "Makanan");
        daftarMenu[1] = new Menu("Ayam Bakar",   35000, "Makanan");
        daftarMenu[2] = new Menu("Mie Goreng",   20000, "Makanan");
        daftarMenu[3] = new Menu("Gado-Gado",    18000, "Makanan");
        daftarMenu[4] = new Menu("Es Teh Manis",  8000, "Minuman");
        daftarMenu[5] = new Menu("Jus Alpukat",  15000, "Minuman");
        daftarMenu[6] = new Menu("Es Jeruk",     10000, "Minuman");
        daftarMenu[7] = new Menu("Air Mineral",   5000, "Minuman");

        Scanner sc = new Scanner(System.in);

        // Tampilkan daftar menu restoran
        tampilkanMenu();

        // Array pesanan: maks 4 slot
        // null  = slot kosong / input tidak valid / dilewati
        Menu[] pesananMenu = new Menu[4];
        int[]  pesananQty  = new int[4];

        // Instruksi pemesanan
        System.out.println("==========================================");
        System.out.println("        MASUKKAN PESANAN ANDA");
        System.out.println("==========================================");
        System.out.println(" Format  : Nama Menu = Jumlah");
        System.out.println(" Contoh  : Nasi Goreng = 2");
        System.out.println(" Lewati  : ketik tanda  -");
        System.out.println(" Catatan : Menu yang sama, gabungkan");
        System.out.println("           jumlahnya dalam satu baris.");
        System.out.println("==========================================\n");

        // Proses 4 slot pesanan secara eksplisit (tanpa perulangan)
        System.out.print("Pesanan 1 : ");
        String inputSlot1 = sc.nextLine();
        prosesSlot(inputSlot1, pesananMenu, pesananQty, 0);

        System.out.print("Pesanan 2 : ");
        String inputSlot2 = sc.nextLine();
        prosesSlot(inputSlot2, pesananMenu, pesananQty, 1);

        System.out.print("Pesanan 3 : ");
        String inputSlot3 = sc.nextLine();
        prosesSlot(inputSlot3, pesananMenu, pesananQty, 2);

        System.out.print("Pesanan 4 : ");
        String inputSlot4 = sc.nextLine();
        prosesSlot(inputSlot4, pesananMenu, pesananQty, 3);

        // Hitung seluruh biaya dan cetak struk
        hitungDanCetakStruk(pesananMenu, pesananQty);

        sc.close();
    }


    // =========================================================
    //  METHOD 1 — tampilkanMenu()
    //
    //  Menampilkan daftar menu yang dikelompokkan berdasarkan
    //  kategori. Mengakses elemen array secara eksplisit
    //  (tanpa perulangan). Memanggil formatRupiah() untuk
    //  setiap harga.
    // =========================================================
    static void tampilkanMenu() {
        System.out.println("==========================================");
        System.out.println("        RESTORAN NUSANTARA RASA");
        System.out.println("             DAFTAR MENU");
        System.out.println("==========================================");
        System.out.println(" MAKANAN");
        System.out.println("------------------------------------------");
        System.out.printf("  1. %-22s %s%n",
                daftarMenu[0].getNama(), formatRupiah(daftarMenu[0].getHarga()));
        System.out.printf("  2. %-22s %s%n",
                daftarMenu[1].getNama(), formatRupiah(daftarMenu[1].getHarga()));
        System.out.printf("  3. %-22s %s%n",
                daftarMenu[2].getNama(), formatRupiah(daftarMenu[2].getHarga()));
        System.out.printf("  4. %-22s %s%n",
                daftarMenu[3].getNama(), formatRupiah(daftarMenu[3].getHarga()));
        System.out.println("==========================================");
        System.out.println(" MINUMAN");
        System.out.println("------------------------------------------");
        System.out.printf("  5. %-22s %s%n",
                daftarMenu[4].getNama(), formatRupiah(daftarMenu[4].getHarga()));
        System.out.printf("  6. %-22s %s%n",
                daftarMenu[5].getNama(), formatRupiah(daftarMenu[5].getHarga()));
        System.out.printf("  7. %-22s %s%n",
                daftarMenu[6].getNama(), formatRupiah(daftarMenu[6].getHarga()));
        System.out.printf("  8. %-22s %s%n",
                daftarMenu[7].getNama(), formatRupiah(daftarMenu[7].getHarga()));
        System.out.println("==========================================");
        System.out.println(" * Promo B1G1 Es Teh Manis (>Rp50.000)");
        System.out.println(" * Diskon 10% untuk total > Rp100.000");
        System.out.println("==========================================\n");
    }


    // =========================================================
    //  METHOD 2 — cariMenu(String nama)
    //
    //  Mencari objek Menu di dalam daftarMenu[] berdasarkan nama.
    //  Menggunakan rangkaian if-else if (indikator 3) sebagai
    //  pengganti perulangan. Tidak peka huruf besar/kecil karena
    //  memakai equalsIgnoreCase(). Mengembalikan null jika tidak
    //  ditemukan. (Indikator 1 & 2 — penggunaan array dan method)
    // =========================================================
    static Menu cariMenu(String nama) {
        if      (daftarMenu[0].getNama().equalsIgnoreCase(nama)) return daftarMenu[0];
        else if (daftarMenu[1].getNama().equalsIgnoreCase(nama)) return daftarMenu[1];
        else if (daftarMenu[2].getNama().equalsIgnoreCase(nama)) return daftarMenu[2];
        else if (daftarMenu[3].getNama().equalsIgnoreCase(nama)) return daftarMenu[3];
        else if (daftarMenu[4].getNama().equalsIgnoreCase(nama)) return daftarMenu[4];
        else if (daftarMenu[5].getNama().equalsIgnoreCase(nama)) return daftarMenu[5];
        else if (daftarMenu[6].getNama().equalsIgnoreCase(nama)) return daftarMenu[6];
        else if (daftarMenu[7].getNama().equalsIgnoreCase(nama)) return daftarMenu[7];
        else return null;
    }


    // =========================================================
    //  METHOD 3 — prosesSlot(...)
    //
    //  Memproses satu slot input pesanan.
    //  Pengolahan String (indikator 2): trim(), contains(),
    //    split("=", 2), isEmpty(), equalsIgnoreCase()
    //  Struktur keputusan (indikator 3):
    //    - if  : cek tanda "-", cek format, cek nama kosong,
    //            cek jumlah positif
    //    - if-else : menu ditemukan atau tidak
    //  Menangani exception NumberFormatException untuk input
    //  bukan angka tanpa menghentikan program.
    // =========================================================
    static void prosesSlot(String input, Menu[] pesananMenu,
                           int[] pesananQty, int slotIndex) {

        String teks = input.trim(); // Hapus spasi di awal/akhir

        // if: slot dilewati dengan tanda "-"
        if (teks.equals("-")) {
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
            System.out.println("  -> Slot " + (slotIndex + 1) + " dilewati.\n");
            return;
        }

        // if: format wajib mengandung karakter '='
        if (!teks.contains("=")) {
            System.out.println("  [ERROR] Slot " + (slotIndex + 1)
                    + ": Format salah. Gunakan: Nama Menu = Jumlah\n");
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
            return;
        }

        // Pisahkan nama menu dan jumlah menggunakan split("=", 2)
        // Batas 2 bagian agar nama menu yang mengandung '=' tidak terpotong
        String[] bagian    = teks.split("=", 2);
        String   namaMenu  = bagian[0].trim();
        String   jumlahStr = bagian[1].trim();

        // if: nama menu tidak boleh kosong
        if (namaMenu.isEmpty()) {
            System.out.println("  [ERROR] Slot " + (slotIndex + 1)
                    + ": Nama menu tidak boleh kosong.\n");
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
            return;
        }

        // Parsing jumlah — tangani input bukan angka
        int jumlah;
        try {
            jumlah = Integer.parseInt(jumlahStr);
        } catch (NumberFormatException e) {
            System.out.println("  [ERROR] Slot " + (slotIndex + 1)
                    + ": \"" + jumlahStr + "\" bukan angka yang valid.\n");
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
            return;
        }

        // if: jumlah harus lebih dari nol
        if (jumlah <= 0) {
            System.out.println("  [ERROR] Slot " + (slotIndex + 1)
                    + ": Jumlah harus bilangan bulat positif (> 0).\n");
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
            return;
        }

        // Cari menu di daftar
        Menu menu = cariMenu(namaMenu);

        // if-else: menu ditemukan atau tidak
        if (menu == null) {
            System.out.println("  [ERROR] Slot " + (slotIndex + 1)
                    + ": Menu \"" + namaMenu + "\" tidak ada dalam daftar.\n");
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
        } else {
            pesananMenu[slotIndex] = menu;
            pesananQty[slotIndex]  = jumlah;
            System.out.println("  [OK] " + menu.getNama() + " x" + jumlah
                    + " (" + menu.getKategori() + ") berhasil ditambahkan.\n");
        }
    }


    // =========================================================
    //  METHOD 4 — hitungSubtotalItem(int harga, int qty)
    //
    //  Menghitung subtotal satu item pesanan: harga × jumlah.
    //  (Indikator 1 — penggunaan method dengan parameter)
    // =========================================================
    static int hitungSubtotalItem(int harga, int qty) {
        return harga * qty;
    }


    // =========================================================
    //  METHOD 5 — hitungDanCetakStruk(...)
    //
    //  Mengorkestrasikan seluruh perhitungan:
    //    - Subtotal per slot (tanpa perulangan)
    //    - Subtotal awal keseluruhan
    //    - Promo B1G1 (nested if — indikator 3)
    //    - Diskon 10% (if-else — indikator 3)
    //    - Pajak dan biaya pelayanan
    //  Kemudian memanggil cetakStruk() untuk mencetak hasil.
    // =========================================================
    static void hitungDanCetakStruk(Menu[] pesananMenu, int[] pesananQty) {

        // Hitung subtotal tiap slot secara eksplisit (tanpa perulangan)
        int sub0 = 0, sub1 = 0, sub2 = 0, sub3 = 0;

        if (pesananMenu[0] != null)
            sub0 = hitungSubtotalItem(pesananMenu[0].getHarga(), pesananQty[0]);
        if (pesananMenu[1] != null)
            sub1 = hitungSubtotalItem(pesananMenu[1].getHarga(), pesananQty[1]);
        if (pesananMenu[2] != null)
            sub2 = hitungSubtotalItem(pesananMenu[2].getHarga(), pesananQty[2]);
        if (pesananMenu[3] != null)
            sub3 = hitungSubtotalItem(pesananMenu[3].getHarga(), pesananQty[3]);

        int subtotalAwal = sub0 + sub1 + sub2 + sub3;

        // if: tidak ada satu pun pesanan yang valid
        if (subtotalAwal == 0) {
            System.out.println("\n[!] Tidak ada pesanan yang valid. Transaksi dibatalkan.");
            return;
        }

        // ==============================================
        //  PROMO BELI SATU GRATIS SATU
        //  Menu promo : Es Teh Manis (asumsi implementasi)
        //  Syarat     : subtotalAwal > Rp50.000 DAN qty >= 2
        // ==============================================

        // Kumpulkan qty menu promo dari semua slot (tanpa perulangan)
        int qtyPromo = 0;
        if (pesananMenu[0] != null
                && pesananMenu[0].getNama().equalsIgnoreCase(NAMA_MENU_PROMO))
            qtyPromo += pesananQty[0];
        if (pesananMenu[1] != null
                && pesananMenu[1].getNama().equalsIgnoreCase(NAMA_MENU_PROMO))
            qtyPromo += pesananQty[1];
        if (pesananMenu[2] != null
                && pesananMenu[2].getNama().equalsIgnoreCase(NAMA_MENU_PROMO))
            qtyPromo += pesananQty[2];
        if (pesananMenu[3] != null
                && pesananMenu[3].getNama().equalsIgnoreCase(NAMA_MENU_PROMO))
            qtyPromo += pesananQty[3];

        // Ambil harga menu promo
        int hargaPromo = 0;
        Menu menuPromo = cariMenu(NAMA_MENU_PROMO);
        if (menuPromo != null) {
            hargaPromo = menuPromo.getHarga();
        }

        // Nested if (indikator 3):
        //   Outer : subtotalAwal > AMBANG_PROMO (syarat nilai transaksi)
        //   Inner : qtyPromo >= 2              (syarat kuantitas mencukupi)
        int    unitGratis       = 0;
        int    penguranganPromo = 0;
        boolean promoAktif      = false;

        if (subtotalAwal > AMBANG_PROMO) {          // Syarat 1: total > Rp50.000
            if (qtyPromo >= 2) {                    // Syarat 2 (nested if): minimal 2 unit
                unitGratis       = qtyPromo / 2;    // Setiap 2 unit → 1 gratis
                penguranganPromo = unitGratis * hargaPromo;
                promoAktif       = true;
            }
        }

        int subtotalSetelahPromo = subtotalAwal - penguranganPromo;

        // ==============================================
        //  DISKON 10%
        //  Syarat: subtotalAwal > Rp100.000
        //  Dihitung dari subtotalSetelahPromo
        // ==============================================
        int    nominalDiskon = 0;
        boolean diskonAktif  = false;

        // if-else (indikator 3): diskon berlaku atau tidak
        if (subtotalAwal > AMBANG_DISKON) {
            nominalDiskon = (int)(subtotalSetelahPromo * TARIF_DISKON);
            diskonAktif   = true;
        } else {
            nominalDiskon = 0;
            diskonAktif   = false;
        }

        int subtotalSetelahDiskon = subtotalSetelahPromo - nominalDiskon;

        // ==============================================
        //  PAJAK & BIAYA PELAYANAN
        // ==============================================
        int dasarPajak   = subtotalSetelahDiskon;
        int nominalPajak = (int)(dasarPajak * TARIF_PAJAK);
        int totalAkhir   = dasarPajak + nominalPajak + BIAYA_PELAYANAN;

        // Cetak struk pembayaran
        cetakStruk(
            pesananMenu, pesananQty,
            sub0, sub1, sub2, sub3,
            subtotalAwal,
            promoAktif, unitGratis, penguranganPromo, subtotalSetelahPromo,
            diskonAktif, nominalDiskon, subtotalSetelahDiskon,
            dasarPajak, nominalPajak, totalAkhir
        );
    }


    // =========================================================
    //  METHOD 6 — cetakStruk(...)
    //
    //  Mencetak struk pembayaran lengkap ke layar.
    //  Menampilkan semua komponen biaya yang dipersyaratkan dosen.
    //  Menggunakan if-else untuk blok promo dan diskon agar
    //  tetap transparan meski tidak berlaku.
    //  Memanggil cetakItemStruk() untuk setiap slot (tanpa loop).
    // =========================================================
    static void cetakStruk(
            Menu[] pesananMenu, int[] pesananQty,
            int sub0, int sub1, int sub2, int sub3,
            int subtotalAwal,
            boolean promoAktif, int unitGratis,
            int penguranganPromo, int subtotalSetelahPromo,
            boolean diskonAktif, int nominalDiskon, int subtotalSetelahDiskon,
            int dasarPajak, int nominalPajak, int totalAkhir) {

        String L = "================================================"; // 48 karakter
        String S = "------------------------------------------------";

        System.out.println("\n" + L);
        System.out.println("         RESTORAN NUSANTARA RASA");
        System.out.println("        Jl. Kuliner Bangsa No. 1");
        System.out.println("               Jakarta");
        System.out.println(L);
        System.out.println("             STRUK PEMBAYARAN");
        System.out.println(S);

        // Cetak setiap slot pesanan secara eksplisit (tanpa perulangan)
        cetakItemStruk(pesananMenu[0], pesananQty[0], sub0);
        cetakItemStruk(pesananMenu[1], pesananQty[1], sub1);
        cetakItemStruk(pesananMenu[2], pesananQty[2], sub2);
        cetakItemStruk(pesananMenu[3], pesananQty[3], sub3);

        System.out.println(S);
        System.out.printf("  %-34s %s%n", "Subtotal Awal", formatRupiah(subtotalAwal));

        // if-else: tampilkan keterangan promo
        if (promoAktif) {
            System.out.printf("  %-34s %s%n",
                "Promo B1G1 (" + unitGratis + "x " + NAMA_MENU_PROMO + ")",
                "-" + formatRupiah(penguranganPromo));
            System.out.printf("  %-34s %s%n",
                "Subtotal Setelah Promo",
                formatRupiah(subtotalSetelahPromo));
        } else {
            System.out.printf("  %-34s %s%n", "Promo B1G1", "Tidak berlaku");
        }

        // if-else: tampilkan keterangan diskon
        if (diskonAktif) {
            System.out.printf("  %-34s %s%n",
                "Diskon 10%",
                "-" + formatRupiah(nominalDiskon));
            System.out.printf("  %-34s %s%n",
                "Subtotal Setelah Diskon",
                formatRupiah(subtotalSetelahDiskon));
        } else {
            System.out.printf("  %-34s %s%n", "Diskon 10%", "Tidak berlaku");
        }

        System.out.println(S);
        System.out.printf("  %-34s %s%n", "Dasar Pengenaan Pajak",
                formatRupiah(dasarPajak));
        System.out.printf("  %-34s %s%n", "PPN (10%)",
                formatRupiah(nominalPajak));
        System.out.printf("  %-34s %s%n", "Biaya Pelayanan",
                formatRupiah(BIAYA_PELAYANAN));
        System.out.println(L);
        System.out.printf("  %-34s %s%n", "TOTAL PEMBAYARAN",
                formatRupiah(totalAkhir));
        System.out.println(L);
        System.out.println("      Terima kasih telah berkunjung!");
        System.out.println("       Selamat menikmati hidangan. :)");
        System.out.println(L);
    }


    // =========================================================
    //  METHOD HELPER — cetakItemStruk(Menu, int, int)
    //
    //  Mencetak satu item pesanan di dalam struk.
    //  Menggunakan switch-case (indikator 3) untuk menentukan
    //  label kategori yang ditampilkan di struk.
    //  Jika slot null (kosong/tidak valid), langsung return.
    // =========================================================
    static void cetakItemStruk(Menu menu, int qty, int subtotal) {
        if (menu == null) return; // Slot kosong, tidak dicetak

        // switch-case: tentukan label kategori untuk ditampilkan
        String labelKategori;
        switch (menu.getKategori()) {
            case "Makanan":
                labelKategori = "Makanan";
                break;
            case "Minuman":
                labelKategori = "Minuman";
                break;
            default:
                labelKategori = "Lainnya";
        }

        // Baris 1: nama menu, label kategori, subtotal
        System.out.printf("  %-20s [%-7s]  %s%n",
                menu.getNama(), labelKategori, formatRupiah(subtotal));
        // Baris 2: qty × harga satuan
        System.out.printf("     %d x %s / porsi%n",
                qty, formatRupiah(menu.getHarga()));
    }


    // =========================================================
    //  METHOD HELPER — formatRupiah(int jumlah)
    //
    //  Memformat angka integer ke string bertanda Rupiah dengan
    //  pemisah ribuan sesuai format Indonesia (titik).
    //  Contoh: 25000 → "Rp25.000"
    //  Menggunakan NumberFormat dengan Locale("id", "ID").
    // =========================================================
    static String formatRupiah(int jumlah) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        return "Rp" + nf.format(jumlah);
    }
          }
