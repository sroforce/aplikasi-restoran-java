import java.util.Scanner;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Main.java — Aplikasi Restoran Sederhana "Nusantara Rasa"
 *
 * Perubahan dari versi sebelumnya:
 *  1. Semua kalkulasi uang menggunakan long (mencegah integer overflow)
 *  2. Batas kuantitas MAX_QTY = 999 ditambahkan ke validasi input
 *  3. Logika B1G1 diperbaiki: 1 unit berbayar → 1 unit tambahan gratis
 *  4. Diskon 10% dihitung dari subtotalAwal, bukan subtotalSetelahPromo
 *  5. Menu sama di beberapa slot digabungkan sebelum dicetak di struk
 *  6. Label pajak diubah menjadi "Pajak (10%)"
 *  7. dasarPajak dijaga agar tidak negatif
 *
 * Urutan perhitungan (asumsi implementasi — tidak dirinci oleh dosen):
 *  1. subtotalAwal        = jumlah (harga × qty) semua item berbayar
 *  2. Kelayakan promo & diskon diuji dari subtotalAwal
 *  3. penguranganPromo    = unitGratis × hargaPromo (B1G1 Es Teh Manis)
 *  4. nominalDiskon       = 10% × subtotalAwal  (jika subtotalAwal > Rp100.000)
 *  5. dasarPajak          = subtotalAwal - penguranganPromo - nominalDiskon (min 0)
 *  6. nominalPajak        = round(10% × dasarPajak)
 *  7. totalAkhir          = dasarPajak + nominalPajak + Rp20.000
 *
 * Program tidak menggunakan for / while / do-while / enhanced-for /
 * forEach / stream / rekursi sesuai arahan dosen.
 */
public class Main {

    // =================================================================
    //  DATA MENU — array objek Menu[8] (indikator 1 & 2)
    // =================================================================
    static Menu[] daftarMenu = new Menu[8];

    // =================================================================
    //  KONSTANTA
    // =================================================================

    /**
     * NAMA_MENU_PROMO: menu yang menjadi objek promo B1G1.
     * ASUMSI IMPLEMENTASI — dosen tidak menetapkan menu promo secara eksplisit.
     * Dipilih Es Teh Manis sebagai menu promo B1G1.
     */
    static final String NAMA_MENU_PROMO = "Es Teh Manis";

    static final long   BIAYA_PELAYANAN = 20_000L;   // Rp20.000 tetap per transaksi
    static final double TARIF_PAJAK     = 0.10;       // 10%
    static final double TARIF_DISKON    = 0.10;       // 10%
    static final long   AMBANG_DISKON   = 100_000L;   // subtotalAwal harus > nilai ini
    static final long   AMBANG_PROMO    =  50_000L;   // subtotalAwal harus > nilai ini

    /**
     * MAX_QTY: batas maksimal jumlah pesanan per slot input.
     * Dipilih 999 — cukup untuk kebutuhan restoran normal sekaligus
     * mencegah nilai ekstrem yang tidak masuk akal.
     * ASUMSI IMPLEMENTASI — tidak disebutkan dalam instruksi dosen.
     */
    static final int MAX_QTY = 999;


    // =================================================================
    //  MAIN — titik masuk program
    // =================================================================
    public static void main(String[] args) {

        // Inisialisasi 8 objek Menu: 4 makanan + 4 minuman
        daftarMenu[0] = new Menu("Nasi Goreng",  25_000L, "Makanan");
        daftarMenu[1] = new Menu("Ayam Bakar",   35_000L, "Makanan");
        daftarMenu[2] = new Menu("Mie Goreng",   20_000L, "Makanan");
        daftarMenu[3] = new Menu("Gado-Gado",    18_000L, "Makanan");
        daftarMenu[4] = new Menu("Es Teh Manis",  8_000L, "Minuman");
        daftarMenu[5] = new Menu("Jus Alpukat",  15_000L, "Minuman");
        daftarMenu[6] = new Menu("Es Jeruk",     10_000L, "Minuman");
        daftarMenu[7] = new Menu("Air Mineral",   5_000L, "Minuman");

        Scanner sc = new Scanner(System.in);

        // Tampilkan daftar menu
        tampilkanMenu();

        // Array pesanan — maks 4 slot
        // null  = slot kosong, dilewati, atau input tidak valid
        Menu[] pesananMenu = new Menu[4];
        int[]  pesananQty  = new int[4];

        // Instruksi kepada pelanggan
        System.out.println("====================================================");
        System.out.println("              MASUKKAN PESANAN ANDA");
        System.out.println("====================================================");
        System.out.println(" Format      : Nama Menu = Jumlah");
        System.out.println(" Contoh      : Nasi Goreng = 2");
        System.out.println(" Lewati slot : ketik tanda -");
        System.out.println(" Maks jumlah : " + MAX_QTY + " per baris pesanan");
        System.out.println(" Menu sama   : gabungkan dalam satu baris");
        System.out.println("====================================================\n");

        // Proses 4 slot secara eksplisit — tanpa perulangan (sesuai arahan dosen)
        System.out.print("Pesanan 1 : ");
        prosesSlot(sc.nextLine(), pesananMenu, pesananQty, 0);

        System.out.print("Pesanan 2 : ");
        prosesSlot(sc.nextLine(), pesananMenu, pesananQty, 1);

        System.out.print("Pesanan 3 : ");
        prosesSlot(sc.nextLine(), pesananMenu, pesananQty, 2);

        System.out.print("Pesanan 4 : ");
        prosesSlot(sc.nextLine(), pesananMenu, pesananQty, 3);

        // Hitung dan cetak struk (penggabungan menu sama dilakukan di dalamnya)
        hitungDanCetakStruk(pesananMenu, pesananQty);

        sc.close();
    }


    // =================================================================
    //  METHOD 1 — tampilkanMenu()
    //
    //  Menampilkan 8 item menu dalam dua kelompok kategori.
    //  Setiap elemen array diakses secara eksplisit (tanpa perulangan).
    // =================================================================
    static void tampilkanMenu() {
        System.out.println("====================================================");
        System.out.println("             RESTORAN NUSANTARA RASA");
        System.out.println("                  DAFTAR MENU");
        System.out.println("====================================================");
        System.out.println(" MAKANAN");
        System.out.println("----------------------------------------------------");
        System.out.printf("  1. %-22s %s%n",
                daftarMenu[0].getNama(), formatRupiah(daftarMenu[0].getHarga()));
        System.out.printf("  2. %-22s %s%n",
                daftarMenu[1].getNama(), formatRupiah(daftarMenu[1].getHarga()));
        System.out.printf("  3. %-22s %s%n",
                daftarMenu[2].getNama(), formatRupiah(daftarMenu[2].getHarga()));
        System.out.printf("  4. %-22s %s%n",
                daftarMenu[3].getNama(), formatRupiah(daftarMenu[3].getHarga()));
        System.out.println("====================================================");
        System.out.println(" MINUMAN");
        System.out.println("----------------------------------------------------");
        System.out.printf("  5. %-22s %s%n",
                daftarMenu[4].getNama(), formatRupiah(daftarMenu[4].getHarga()));
        System.out.printf("  6. %-22s %s%n",
                daftarMenu[5].getNama(), formatRupiah(daftarMenu[5].getHarga()));
        System.out.printf("  7. %-22s %s%n",
                daftarMenu[6].getNama(), formatRupiah(daftarMenu[6].getHarga()));
        System.out.printf("  8. %-22s %s%n",
                daftarMenu[7].getNama(), formatRupiah(daftarMenu[7].getHarga()));
        System.out.println("====================================================");
        System.out.println(" * Promo B1G1 Es Teh Manis berlaku jika total > Rp50.000");
        System.out.println(" * Diskon 10% berlaku jika total > Rp100.000");
        System.out.println("====================================================\n");
    }


    // =================================================================
    //  METHOD 2 — cariMenu(String nama)
    //
    //  Mencari objek Menu berdasarkan nama menggunakan rangkaian
    //  if-else if (indikator 3) sebagai pengganti perulangan.
    //  Menggunakan equalsIgnoreCase() agar tidak peka huruf besar/kecil.
    //  Mengembalikan null jika nama tidak ditemukan.
    // =================================================================
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


    // =================================================================
    //  METHOD 3 — prosesSlot(...)
    //
    //  Memproses dan memvalidasi satu baris input pesanan.
    //
    //  Pengolahan String (indikator 2):
    //    trim(), equals(), contains(), split("=", 2),
    //    isEmpty(), equalsIgnoreCase(), Integer.parseInt()
    //
    //  Struktur keputusan (indikator 3):
    //    if         — cek "-", format, nama kosong, qty ≤ 0, qty > MAX
    //    if-else    — menu ditemukan atau tidak
    //    try-catch  — menangani input bukan angka
    // =================================================================
    static void prosesSlot(String input, Menu[] pesananMenu,
                           int[] pesananQty, int slotIndex) {

        String teks = input.trim(); // Hapus spasi awal dan akhir

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

        // Pisahkan nama menu dan jumlah — batas 2 bagian mencegah
        // nama menu yang mengandung '=' ikut terpotong
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

        // Parsing jumlah — tangani input bukan angka tanpa menghentikan program
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

        // if: jumlah harus bilangan bulat positif
        if (jumlah <= 0) {
            System.out.println("  [ERROR] Slot " + (slotIndex + 1)
                    + ": Jumlah harus lebih dari 0.\n");
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
            return;
        }

        // if: jumlah tidak boleh melebihi batas maksimal
        if (jumlah > MAX_QTY) {
            System.out.println("  [ERROR] Slot " + (slotIndex + 1)
                    + ": Jumlah maksimal per pesanan adalah " + MAX_QTY + ".\n");
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
            return;
        }

        // Cari nama menu di dalam daftarMenu[]
        Menu menu = cariMenu(namaMenu);

        // if-else: menu ditemukan atau tidak
        if (menu == null) {
            System.out.println("  [ERROR] Slot " + (slotIndex + 1)
                    + ": Menu \"" + namaMenu + "\" tidak tersedia dalam daftar.\n");
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
        } else {
            pesananMenu[slotIndex] = menu;
            pesananQty[slotIndex]  = jumlah;
            System.out.println("  [OK] " + menu.getNama()
                    + " x" + jumlah
                    + " (" + menu.getKategori() + ") ditambahkan.\n");
        }
    }


    // =================================================================
    //  METHOD 4 — gabungkanSlotSama(...)
    //
    //  Menggabungkan kuantitas slot yang memiliki menu yang sama.
    //  Dari 4 slot, terdapat 6 kombinasi pasangan yang mungkin.
    //  Semua diperiksa secara eksplisit — tanpa perulangan.
    //  Slot lebih awal menjadi tempat gabungan; slot lebih belakang
    //  di-null setelah digabungkan.
    //  Dipanggil di awal hitungDanCetakStruk sebelum kalkulasi apa pun.
    // =================================================================
    static void gabungkanSlotSama(Menu[] pesananMenu, int[] pesananQty) {

        // Slot 0 vs Slot 1
        if (pesananMenu[0] != null && pesananMenu[1] != null
                && pesananMenu[0].getNama().equalsIgnoreCase(pesananMenu[1].getNama())) {
            pesananQty[0] += pesananQty[1];
            pesananMenu[1] = null;
            pesananQty[1]  = 0;
        }
        // Slot 0 vs Slot 2
        if (pesananMenu[0] != null && pesananMenu[2] != null
                && pesananMenu[0].getNama().equalsIgnoreCase(pesananMenu[2].getNama())) {
            pesananQty[0] += pesananQty[2];
            pesananMenu[2] = null;
            pesananQty[2]  = 0;
        }
        // Slot 0 vs Slot 3
        if (pesananMenu[0] != null && pesananMenu[3] != null
                && pesananMenu[0].getNama().equalsIgnoreCase(pesananMenu[3].getNama())) {
            pesananQty[0] += pesananQty[3];
            pesananMenu[3] = null;
            pesananQty[3]  = 0;
        }
        // Slot 1 vs Slot 2
        if (pesananMenu[1] != null && pesananMenu[2] != null
                && pesananMenu[1].getNama().equalsIgnoreCase(pesananMenu[2].getNama())) {
            pesananQty[1] += pesananQty[2];
            pesananMenu[2] = null;
            pesananQty[2]  = 0;
        }
        // Slot 1 vs Slot 3
        if (pesananMenu[1] != null && pesananMenu[3] != null
                && pesananMenu[1].getNama().equalsIgnoreCase(pesananMenu[3].getNama())) {
            pesananQty[1] += pesananQty[3];
            pesananMenu[3] = null;
            pesananQty[3]  = 0;
        }
        // Slot 2 vs Slot 3
        if (pesananMenu[2] != null && pesananMenu[3] != null
                && pesananMenu[2].getNama().equalsIgnoreCase(pesananMenu[3].getNama())) {
            pesananQty[2] += pesananQty[3];
            pesananMenu[3] = null;
            pesananQty[3]  = 0;
        }
    }


    // =================================================================
    //  METHOD 5 — hitungSubtotalItem(long harga, int qty)
    //
    //  Menghitung subtotal satu item: harga satuan × jumlah pesanan.
    //  Mengembalikan long untuk konsistensi dan keamanan overflow.
    // =================================================================
    static long hitungSubtotalItem(long harga, int qty) {
        return harga * qty;
    }


    // =================================================================
    //  METHOD 6 — hitungDanCetakStruk(...)
    //
    //  Mengorkestrasikan seluruh proses pasca-input:
    //    1. Gabungkan slot dengan menu sama
    //    2. Hitung subtotal tiap slot
    //    3. Hitung subtotalAwal
    //    4. Promo B1G1 (nested if)
    //    5. Diskon 10% (if-else)
    //    6. Dasar pajak, pajak, total akhir
    //    7. Panggil cetakStruk
    // =================================================================
    static void hitungDanCetakStruk(Menu[] pesananMenu, int[] pesananQty) {

        // Langkah 1: gabungkan slot dengan menu yang sama
        gabungkanSlotSama(pesananMenu, pesananQty);

        // Langkah 2: hitung subtotal tiap slot secara eksplisit (tanpa perulangan)
        long sub0 = 0L, sub1 = 0L, sub2 = 0L, sub3 = 0L;
        if (pesananMenu[0] != null)
            sub0 = hitungSubtotalItem(pesananMenu[0].getHarga(), pesananQty[0]);
        if (pesananMenu[1] != null)
            sub1 = hitungSubtotalItem(pesananMenu[1].getHarga(), pesananQty[1]);
        if (pesananMenu[2] != null)
            sub2 = hitungSubtotalItem(pesananMenu[2].getHarga(), pesananQty[2]);
        if (pesananMenu[3] != null)
            sub3 = hitungSubtotalItem(pesananMenu[3].getHarga(), pesananQty[3]);

        long subtotalAwal = sub0 + sub1 + sub2 + sub3;

        // if: tidak ada pesanan valid → batalkan transaksi
        if (subtotalAwal == 0L) {
            System.out.println("\n[!] Tidak ada pesanan yang valid. Transaksi dibatalkan.");
            return;
        }

        // -----------------------------------------------------------
        //  Langkah 3: PROMO BELI SATU GRATIS SATU
        //
        //  Menu promo  : Es Teh Manis (asumsi implementasi)
        //  Syarat      : subtotalAwal > Rp50.000  DAN  qtyPromo >= 1
        //  Mekanisme   : setiap 1 unit berbayar → 1 unit tambahan gratis
        //                (pelanggan pesan N, terima 2N, bayar N)
        //
        //  Nested if (indikator 3):
        //    Outer : uji nilai transaksi  (subtotalAwal > AMBANG_PROMO)
        //    Inner : uji kuantitas promo  (qtyPromo >= 1)
        // -----------------------------------------------------------

        // Kumpulkan total qty Es Teh Manis dari semua slot (tanpa perulangan)
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

        // Ambil harga menu promo dari daftarMenu
        long  hargaPromo = 0L;
        Menu  menuPromo  = cariMenu(NAMA_MENU_PROMO);
        if (menuPromo != null) hargaPromo = menuPromo.getHarga();

        int     unitGratis       = 0;
        long    penguranganPromo = 0L;
        boolean promoAktif       = false;

        if (subtotalAwal > AMBANG_PROMO) {         // Outer if: nilai transaksi
            if (qtyPromo >= 1) {                   // Inner if (nested): ada menu promo
                unitGratis       = qtyPromo;       // 1 berbayar = 1 gratis
                penguranganPromo = (long) unitGratis * hargaPromo;
                promoAktif       = true;
            }
        }

        // -----------------------------------------------------------
        //  Langkah 4: DISKON 10%
        //  Syarat  : subtotalAwal > Rp100.000 (operator >, bukan >=)
        //  Besaran : 10% dari subtotalAwal
        // -----------------------------------------------------------
        long    nominalDiskon = 0L;
        boolean diskonAktif   = false;

        // if-else (indikator 3): diskon berlaku atau tidak
        if (subtotalAwal > AMBANG_DISKON) {
            nominalDiskon = Math.round(subtotalAwal * TARIF_DISKON);
            diskonAktif   = true;
        } else {
            nominalDiskon = 0L;
            diskonAktif   = false;
        }

        // -----------------------------------------------------------
        //  Langkah 5 – 7: DASAR PAJAK, PAJAK, TOTAL AKHIR
        //
        //  dasarPajak = subtotalAwal - penguranganPromo - nominalDiskon
        //  Dijaga ≥ 0: jika potongan melebihi subtotal, dasarPajak = 0
        //  Pajak dibulatkan ke rupiah terdekat (Math.round)
        // -----------------------------------------------------------
        long dasarPajak = subtotalAwal - penguranganPromo - nominalDiskon;
        if (dasarPajak < 0L) dasarPajak = 0L;

        long nominalPajak = Math.round(dasarPajak * TARIF_PAJAK);
        long totalAkhir   = dasarPajak + nominalPajak + BIAYA_PELAYANAN;

        // Cetak struk pembayaran
        cetakStruk(
            pesananMenu, pesananQty,
            sub0, sub1, sub2, sub3,
            subtotalAwal,
            promoAktif, unitGratis, penguranganPromo,
            diskonAktif, nominalDiskon,
            dasarPajak, nominalPajak, totalAkhir
        );
    }


    // =================================================================
    //  METHOD 7 — cetakStruk(...)
    //
    //  Mencetak struk pembayaran lengkap ke layar.
    //  Setiap slot dicetak secara eksplisit melalui cetakItemStruk.
    //  if-else digunakan untuk blok promo dan diskon agar struk
    //  tetap informatif bahkan ketika potongan tidak berlaku.
    // =================================================================
    static void cetakStruk(
            Menu[] pesananMenu, int[] pesananQty,
            long sub0, long sub1, long sub2, long sub3,
            long subtotalAwal,
            boolean promoAktif, int unitGratis, long penguranganPromo,
            boolean diskonAktif, long nominalDiskon,
            long dasarPajak, long nominalPajak, long totalAkhir) {

        final String L = "====================================================";
        final String S = "----------------------------------------------------";

        System.out.println("\n" + L);
        System.out.println("            RESTORAN NUSANTARA RASA");
        System.out.println("           Jl. Kuliner Bangsa No. 1");
        System.out.println("                   Jakarta");
        System.out.println(L);
        System.out.println("                STRUK PEMBAYARAN");
        System.out.println(S);

        // Cetak tiap slot pesanan secara eksplisit (tanpa perulangan)
        cetakItemStruk(pesananMenu[0], pesananQty[0], sub0);
        cetakItemStruk(pesananMenu[1], pesananQty[1], sub1);
        cetakItemStruk(pesananMenu[2], pesananQty[2], sub2);
        cetakItemStruk(pesananMenu[3], pesananQty[3], sub3);

        System.out.println(S);
        System.out.printf("  %-36s %s%n", "Subtotal Awal", formatRupiah(subtotalAwal));

        // if-else: tampilkan informasi promo
        if (promoAktif) {
            System.out.printf("  %-36s %s%n",
                "Promo B1G1 (" + unitGratis + "x " + NAMA_MENU_PROMO + " gratis)",
                "-" + formatRupiah(penguranganPromo));
            System.out.println("    [Beli " + unitGratis
                + " unit, dapat " + unitGratis + " unit tambahan gratis]");
        } else {
            System.out.printf("  %-36s %s%n", "Promo B1G1", "Tidak berlaku");
        }

        // if-else: tampilkan informasi diskon
        if (diskonAktif) {
            System.out.printf("  %-36s %s%n",
                "Diskon 10% (dari subtotal awal)",
                "-" + formatRupiah(nominalDiskon));
        } else {
            System.out.printf("  %-36s %s%n", "Diskon 10%", "Tidak berlaku");
        }

        System.out.println(S);
        System.out.printf("  %-36s %s%n", "Dasar Pengenaan Pajak", formatRupiah(dasarPajak));
        System.out.printf("  %-36s %s%n", "Pajak (10%)",           formatRupiah(nominalPajak));
        System.out.printf("  %-36s %s%n", "Biaya Pelayanan",       formatRupiah(BIAYA_PELAYANAN));
        System.out.println(L);
        System.out.printf("  %-36s %s%n", "TOTAL PEMBAYARAN",      formatRupiah(totalAkhir));
        System.out.println(L);
        System.out.println("         Terima kasih telah berkunjung!");
        System.out.println("          Selamat menikmati hidangan. :)");
        System.out.println(L);
    }


    // =================================================================
    //  METHOD 8 — cetakItemStruk(Menu, int, long)
    //
    //  Mencetak satu baris item pesanan dalam struk.
    //  Menggunakan switch-case untuk label kategori (indikator 3).
    //  Slot null (kosong atau tidak valid) tidak dicetak.
    // =================================================================
    static void cetakItemStruk(Menu menu, int qty, long subtotal) {
        if (menu == null) return; // Slot kosong — tidak dicetak

        // switch-case (indikator 3): tentukan label kategori
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

        // Baris 1: nama menu, kategori, subtotal
        System.out.printf("  %-22s [%-7s]  %s%n",
                menu.getNama(), labelKategori, formatRupiah(subtotal));
        // Baris 2: kuantitas dan harga satuan
        System.out.printf("     %d x %s/porsi%n",
                qty, formatRupiah(menu.getHarga()));
    }


    // =================================================================
    //  METHOD HELPER — formatRupiah(long)
    //
    //  Memformat angka long ke format Rupiah Indonesia.
    //  Menggunakan Locale("id", "ID") agar pemisah ribuan berupa titik.
    //  Contoh: 25000 → "Rp25.000", 146940 → "Rp146.940"
    // =================================================================
    static String formatRupiah(long jumlah) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        nf.setGroupingUsed(true);
        return "Rp" + nf.format(jumlah);
    }
}
