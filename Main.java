import java.util.Scanner;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Main.java — Aplikasi Restoran Sederhana "Nusantara Rasa"
 *
 * [v4 — perbaikan bug gabungkanSlotSama]
 *
 * Perubahan dari v3:
 *  Satu-satunya perubahan ada pada method gabungkanSlotSama().
 *  Bug: ketika pasangan slot (i,j) dibatalkan karena kuantitas gabungan
 *  melebihi MAX_QTY, slot lain yang berisi menu yang sama tidak ikut
 *  dibatalkan karena pemeriksaan pasangannya dilewati akibat kondisi
 *  null check gagal.
 *  Perbaikan: saat overflow memicu pembatalan pasangan (i,j), kode kini
 *  secara eksplisit memeriksa dan membatalkan dua slot sisanya jika
 *  namanya sama. Tidak ada bagian lain dari program yang diubah.
 *
 * ---------------------------------------------------------------
 *  URUTAN PERHITUNGAN (asumsi implementasi — dosen tidak merinci)
 * ---------------------------------------------------------------
 *  1. subtotalAwal     = Σ(harga × qty) semua item berbayar yang diterima
 *  2. Kelayakan promo  = subtotalAwal > Rp50.000
 *  3. Kelayakan diskon = subtotalAwal > Rp100.000
 *  4. nominalDiskon    = round(10% × subtotalAwal)  [jika memenuhi syarat]
 *  5. dasarPajak       = subtotalAwal − nominalDiskon  (dijaga ≥ 0)
 *  6. nominalPajak     = round(10% × dasarPajak)
 *  7. totalAkhir       = dasarPajak + nominalPajak + Rp20.000
 *
 *  Promo B1G1 menambahkan unit fisik gratis dan menampilkan nilai
 *  manfaatnya di struk. Promo TIDAK mengurangi subtotalAwal,
 *  dasarPajak, maupun totalAkhir.
 * ---------------------------------------------------------------
 *
 * Program tidak menggunakan for / while / do-while / enhanced-for /
 * forEach / stream / rekursi sesuai arahan dosen.
 */
public class Main {

    // =================================================================
    //  DATA MENU — array objek Menu[8] (indikator 1 & 2)
    // =================================================================
    static Menu[] daftarMenu = new Menu[8];

    /**
     * NAMA_MENU_PROMO: menu objek promo B1G1.
     * ASUMSI IMPLEMENTASI — dosen tidak menetapkan menu promo secara eksplisit.
     * Dipilih "Es Teh Manis".
     */
    static final String NAMA_MENU_PROMO = "Es Teh Manis";

    static final long   BIAYA_PELAYANAN = 20_000L;
    static final double TARIF_PAJAK     = 0.10;
    static final double TARIF_DISKON    = 0.10;
    static final long   AMBANG_DISKON   = 100_000L;
    static final long   AMBANG_PROMO    =  50_000L;

    /**
     * MAX_QTY: batas maksimal kuantitas per slot input DAN setelah
     * penggabungan menu yang sama.
     * ASUMSI IMPLEMENTASI — tidak disebutkan dalam instruksi dosen.
     * Dipilih 999: cukup untuk kebutuhan restoran normal; nilai aman
     * dalam long (harga tertinggi × 999 × 4 slot ≈ Rp140 juta,
     * jauh di bawah Long.MAX_VALUE).
     */
    static final int MAX_QTY = 999;


    // =================================================================
    //  MAIN
    // =================================================================
    public static void main(String[] args) {

        daftarMenu[0] = new Menu("Nasi Goreng",  25_000L, "Makanan");
        daftarMenu[1] = new Menu("Ayam Bakar",   35_000L, "Makanan");
        daftarMenu[2] = new Menu("Mie Goreng",   20_000L, "Makanan");
        daftarMenu[3] = new Menu("Gado-Gado",    18_000L, "Makanan");
        daftarMenu[4] = new Menu("Es Teh Manis",  8_000L, "Minuman");
        daftarMenu[5] = new Menu("Jus Alpukat",  15_000L, "Minuman");
        daftarMenu[6] = new Menu("Es Jeruk",     10_000L, "Minuman");
        daftarMenu[7] = new Menu("Air Mineral",   5_000L, "Minuman");

        Scanner sc = new Scanner(System.in);

        tampilkanMenu();

        Menu[] pesananMenu = new Menu[4];
        int[]  pesananQty  = new int[4];

        System.out.println("====================================================");
        System.out.println("              MASUKKAN PESANAN ANDA");
        System.out.println("====================================================");
        System.out.println(" Format      : Nama Menu = Jumlah");
        System.out.println(" Contoh      : Nasi Goreng = 2");
        System.out.println(" Lewati slot : ketik tanda -");
        System.out.println(" Maks jumlah : " + MAX_QTY + " per baris (dan per menu)");
        System.out.println(" Menu sama   : gabungkan dalam satu baris");
        System.out.println("====================================================\n");

        System.out.print("Pesanan 1 : ");
        prosesSlot(sc.nextLine(), pesananMenu, pesananQty, 0);

        System.out.print("Pesanan 2 : ");
        prosesSlot(sc.nextLine(), pesananMenu, pesananQty, 1);

        System.out.print("Pesanan 3 : ");
        prosesSlot(sc.nextLine(), pesananMenu, pesananQty, 2);

        System.out.print("Pesanan 4 : ");
        prosesSlot(sc.nextLine(), pesananMenu, pesananQty, 3);

        hitungDanCetakStruk(pesananMenu, pesananQty);

        sc.close();
    }


    // =================================================================
    //  METHOD 1 — tampilkanMenu()
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
        System.out.println(" * Promo B1G1 Es Teh Manis jika total > Rp50.000");
        System.out.println(" * Diskon 10% jika total > Rp100.000");
        System.out.println("====================================================\n");
    }


    // =================================================================
    //  METHOD 2 — cariMenu(String)
    //
    //  Mencari objek Menu berdasarkan nama menggunakan rangkaian
    //  if-else if (indikator 3) sebagai pengganti perulangan.
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
    //  Pengolahan String (indikator 2):
    //    trim(), equals(), contains(), split("=", 2),
    //    isEmpty(), equalsIgnoreCase(), Integer.parseInt()
    //
    //  Struktur keputusan (indikator 3):
    //    if       — cek "-", format, nama kosong, qty ≤ 0, qty > MAX_QTY
    //    if-else  — menu ditemukan atau tidak
    //    try-catch — menangani input bukan angka
    // =================================================================
    static void prosesSlot(String input, Menu[] pesananMenu,
                           int[] pesananQty, int slotIndex) {

        String teks = input.trim();

        if (teks.equals("-")) {
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
            System.out.println("  -> Slot " + (slotIndex + 1) + " dilewati.\n");
            return;
        }

        if (!teks.contains("=")) {
            System.out.println("  [ERROR] Slot " + (slotIndex + 1)
                    + ": Format salah. Gunakan: Nama Menu = Jumlah\n");
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
            return;
        }

        String[] bagian    = teks.split("=", 2);
        String   namaMenu  = bagian[0].trim();
        String   jumlahStr = bagian[1].trim();

        if (namaMenu.isEmpty()) {
            System.out.println("  [ERROR] Slot " + (slotIndex + 1)
                    + ": Nama menu tidak boleh kosong.\n");
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
            return;
        }

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

        if (jumlah <= 0) {
            System.out.println("  [ERROR] Slot " + (slotIndex + 1)
                    + ": Jumlah harus lebih dari 0.\n");
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
            return;
        }

        if (jumlah > MAX_QTY) {
            System.out.println("  [ERROR] Slot " + (slotIndex + 1)
                    + ": Jumlah maksimal per pesanan adalah " + MAX_QTY + ".\n");
            pesananMenu[slotIndex] = null;
            pesananQty[slotIndex]  = 0;
            return;
        }

        Menu menu = cariMenu(namaMenu);

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
    //  Menggabungkan kuantitas slot yang memiliki nama menu yang sama.
    //  Memeriksa 6 kombinasi pasangan dari 4 slot secara eksplisit —
    //  tanpa perulangan.
    //
    //  Slot lebih awal menjadi tempat penampung gabungan;
    //  slot lebih belakang di-null setelah digabungkan.
    //
    //  [v4 — PERBAIKAN BUG]
    //  Sebelumnya: saat pasangan (i,j) dibatalkan karena overflow,
    //  slot lain yang memiliki menu yang sama tidak ikut dibatalkan
    //  karena pasangannya dilewati akibat null check.
    //  Sekarang: saat overflow memicu pembatalan (i,j), kode secara
    //  eksplisit memeriksa dan membatalkan dua slot sisanya jika
    //  namanya sama. Ini memastikan tidak ada sisa slot dengan menu
    //  yang sama yang lolos ke perhitungan.
    // =================================================================
    static void gabungkanSlotSama(Menu[] pesananMenu, int[] pesananQty) {

        // ------ Slot 0 vs Slot 1 ------
        if (pesananMenu[0] != null && pesananMenu[1] != null
                && pesananMenu[0].getNama().equalsIgnoreCase(pesananMenu[1].getNama())) {
            int gabungan = pesananQty[0] + pesananQty[1];
            if (gabungan > MAX_QTY) {
                String namaBatal = pesananMenu[0].getNama();
                System.out.println("  [PERINGATAN] Jumlah gabungan \""
                        + namaBatal + "\" (" + gabungan
                        + ") melampaui batas maksimum " + MAX_QTY
                        + ". Pesanan untuk menu ini dibatalkan.\n");
                pesananMenu[0] = null; pesananQty[0] = 0;
                pesananMenu[1] = null; pesananQty[1] = 0;
                // Batalkan juga slot 2 dan 3 jika memiliki menu yang sama
                if (pesananMenu[2] != null
                        && pesananMenu[2].getNama().equalsIgnoreCase(namaBatal)) {
                    pesananMenu[2] = null; pesananQty[2] = 0;
                }
                if (pesananMenu[3] != null
                        && pesananMenu[3].getNama().equalsIgnoreCase(namaBatal)) {
                    pesananMenu[3] = null; pesananQty[3] = 0;
                }
            } else {
                pesananQty[0]  = gabungan;
                pesananMenu[1] = null; pesananQty[1] = 0;
            }
        }

        // ------ Slot 0 vs Slot 2 ------
        if (pesananMenu[0] != null && pesananMenu[2] != null
                && pesananMenu[0].getNama().equalsIgnoreCase(pesananMenu[2].getNama())) {
            int gabungan = pesananQty[0] + pesananQty[2];
            if (gabungan > MAX_QTY) {
                String namaBatal = pesananMenu[0].getNama();
                System.out.println("  [PERINGATAN] Jumlah gabungan \""
                        + namaBatal + "\" (" + gabungan
                        + ") melampaui batas maksimum " + MAX_QTY
                        + ". Pesanan untuk menu ini dibatalkan.\n");
                pesananMenu[0] = null; pesananQty[0] = 0;
                pesananMenu[2] = null; pesananQty[2] = 0;
                // Batalkan juga slot 1 dan 3 jika memiliki menu yang sama
                if (pesananMenu[1] != null
                        && pesananMenu[1].getNama().equalsIgnoreCase(namaBatal)) {
                    pesananMenu[1] = null; pesananQty[1] = 0;
                }
                if (pesananMenu[3] != null
                        && pesananMenu[3].getNama().equalsIgnoreCase(namaBatal)) {
                    pesananMenu[3] = null; pesananQty[3] = 0;
                }
            } else {
                pesananQty[0]  = gabungan;
                pesananMenu[2] = null; pesananQty[2] = 0;
            }
        }

        // ------ Slot 0 vs Slot 3 ------
        if (pesananMenu[0] != null && pesananMenu[3] != null
                && pesananMenu[0].getNama().equalsIgnoreCase(pesananMenu[3].getNama())) {
            int gabungan = pesananQty[0] + pesananQty[3];
            if (gabungan > MAX_QTY) {
                String namaBatal = pesananMenu[0].getNama();
                System.out.println("  [PERINGATAN] Jumlah gabungan \""
                        + namaBatal + "\" (" + gabungan
                        + ") melampaui batas maksimum " + MAX_QTY
                        + ". Pesanan untuk menu ini dibatalkan.\n");
                pesananMenu[0] = null; pesananQty[0] = 0;
                pesananMenu[3] = null; pesananQty[3] = 0;
                // Batalkan juga slot 1 dan 2 jika memiliki menu yang sama
                if (pesananMenu[1] != null
                        && pesananMenu[1].getNama().equalsIgnoreCase(namaBatal)) {
                    pesananMenu[1] = null; pesananQty[1] = 0;
                }
                if (pesananMenu[2] != null
                        && pesananMenu[2].getNama().equalsIgnoreCase(namaBatal)) {
                    pesananMenu[2] = null; pesananQty[2] = 0;
                }
            } else {
                pesananQty[0]  = gabungan;
                pesananMenu[3] = null; pesananQty[3] = 0;
            }
        }

        // ------ Slot 1 vs Slot 2 ------
        if (pesananMenu[1] != null && pesananMenu[2] != null
                && pesananMenu[1].getNama().equalsIgnoreCase(pesananMenu[2].getNama())) {
            int gabungan = pesananQty[1] + pesananQty[2];
            if (gabungan > MAX_QTY) {
                String namaBatal = pesananMenu[1].getNama();
                System.out.println("  [PERINGATAN] Jumlah gabungan \""
                        + namaBatal + "\" (" + gabungan
                        + ") melampaui batas maksimum " + MAX_QTY
                        + ". Pesanan untuk menu ini dibatalkan.\n");
                pesananMenu[1] = null; pesananQty[1] = 0;
                pesananMenu[2] = null; pesananQty[2] = 0;
                // Batalkan juga slot 0 dan 3 jika memiliki menu yang sama
                if (pesananMenu[0] != null
                        && pesananMenu[0].getNama().equalsIgnoreCase(namaBatal)) {
                    pesananMenu[0] = null; pesananQty[0] = 0;
                }
                if (pesananMenu[3] != null
                        && pesananMenu[3].getNama().equalsIgnoreCase(namaBatal)) {
                    pesananMenu[3] = null; pesananQty[3] = 0;
                }
            } else {
                pesananQty[1]  = gabungan;
                pesananMenu[2] = null; pesananQty[2] = 0;
            }
        }

        // ------ Slot 1 vs Slot 3 ------
        if (pesananMenu[1] != null && pesananMenu[3] != null
                && pesananMenu[1].getNama().equalsIgnoreCase(pesananMenu[3].getNama())) {
            int gabungan = pesananQty[1] + pesananQty[3];
            if (gabungan > MAX_QTY) {
                String namaBatal = pesananMenu[1].getNama();
                System.out.println("  [PERINGATAN] Jumlah gabungan \""
                        + namaBatal + "\" (" + gabungan
                        + ") melampaui batas maksimum " + MAX_QTY
                        + ". Pesanan untuk menu ini dibatalkan.\n");
                pesananMenu[1] = null; pesananQty[1] = 0;
                pesananMenu[3] = null; pesananQty[3] = 0;
                // Batalkan juga slot 0 dan 2 jika memiliki menu yang sama
                if (pesananMenu[0] != null
                        && pesananMenu[0].getNama().equalsIgnoreCase(namaBatal)) {
                    pesananMenu[0] = null; pesananQty[0] = 0;
                }
                if (pesananMenu[2] != null
                        && pesananMenu[2].getNama().equalsIgnoreCase(namaBatal)) {
                    pesananMenu[2] = null; pesananQty[2] = 0;
                }
            } else {
                pesananQty[1]  = gabungan;
                pesananMenu[3] = null; pesananQty[3] = 0;
            }
        }

        // ------ Slot 2 vs Slot 3 ------
        if (pesananMenu[2] != null && pesananMenu[3] != null
                && pesananMenu[2].getNama().equalsIgnoreCase(pesananMenu[3].getNama())) {
            int gabungan = pesananQty[2] + pesananQty[3];
            if (gabungan > MAX_QTY) {
                String namaBatal = pesananMenu[2].getNama();
                System.out.println("  [PERINGATAN] Jumlah gabungan \""
                        + namaBatal + "\" (" + gabungan
                        + ") melampaui batas maksimum " + MAX_QTY
                        + ". Pesanan untuk menu ini dibatalkan.\n");
                pesananMenu[2] = null; pesananQty[2] = 0;
                pesananMenu[3] = null; pesananQty[3] = 0;
                // Batalkan juga slot 0 dan 1 jika memiliki menu yang sama
                if (pesananMenu[0] != null
                        && pesananMenu[0].getNama().equalsIgnoreCase(namaBatal)) {
                    pesananMenu[0] = null; pesananQty[0] = 0;
                }
                if (pesananMenu[1] != null
                        && pesananMenu[1].getNama().equalsIgnoreCase(namaBatal)) {
                    pesananMenu[1] = null; pesananQty[1] = 0;
                }
            } else {
                pesananQty[2]  = gabungan;
                pesananMenu[3] = null; pesananQty[3] = 0;
            }
        }
    }


    // =================================================================
    //  METHOD 5 — hitungSubtotalItem(long, int)
    // =================================================================
    static long hitungSubtotalItem(long harga, int qty) {
        return harga * qty;
    }


    // =================================================================
    //  METHOD 6 — hitungDanCetakStruk(...)
    // =================================================================
    static void hitungDanCetakStruk(Menu[] pesananMenu, int[] pesananQty) {

        // Langkah 1: gabungkan slot dengan menu yang sama
        gabungkanSlotSama(pesananMenu, pesananQty);

        // Langkah 2: hitung subtotal tiap slot secara eksplisit
        long sub0 = 0L, sub1 = 0L, sub2 = 0L, sub3 = 0L;
        if (pesananMenu[0] != null)
            sub0 = hitungSubtotalItem(pesananMenu[0].getHarga(), pesananQty[0]);
        if (pesananMenu[1] != null)
            sub1 = hitungSubtotalItem(pesananMenu[1].getHarga(), pesananQty[1]);
        if (pesananMenu[2] != null)
            sub2 = hitungSubtotalItem(pesananMenu[2].getHarga(), pesananQty[2]);
        if (pesananMenu[3] != null)
            sub3 = hitungSubtotalItem(pesananMenu[3].getHarga(), pesananQty[3]);

        // Langkah 3: subtotal awal seluruh item berbayar yang diterima
        long subtotalAwal = sub0 + sub1 + sub2 + sub3;

        if (subtotalAwal == 0L) {
            System.out.println("\n[!] Tidak ada pesanan yang valid. Transaksi dibatalkan.");
            return;
        }

        // =============================================================
        //  Langkah 4: PROMO B1G1 (informatif — tidak mengurangi tunai)
        //
        //  ASUMSI: Es Teh Manis sebagai menu promo (dosen tidak merinci)
        //  1 unit berbayar → 1 unit tambahan gratis
        //
        //  Nested if (indikator 3):
        //    Outer : subtotalAwal > AMBANG_PROMO
        //    Inner : qtyPromo >= 1
        // =============================================================
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

        long hargaPromo = 0L;
        Menu menuPromo  = cariMenu(NAMA_MENU_PROMO);
        if (menuPromo != null) hargaPromo = menuPromo.getHarga();

        int     unitGratis        = 0;
        long    nilaiManfaatPromo = 0L;
        boolean promoAktif        = false;

        if (subtotalAwal > AMBANG_PROMO) {
            if (qtyPromo >= 1) {
                unitGratis        = qtyPromo;
                nilaiManfaatPromo = (long) unitGratis * hargaPromo;
                promoAktif        = true;
            }
        }

        // =============================================================
        //  Langkah 5: DISKON 10%
        //  if-else (indikator 3)
        // =============================================================
        long    nominalDiskon = 0L;
        boolean diskonAktif   = false;

        if (subtotalAwal > AMBANG_DISKON) {
            nominalDiskon = Math.round(subtotalAwal * TARIF_DISKON);
            diskonAktif   = true;
        } else {
            nominalDiskon = 0L;
            diskonAktif   = false;
        }

        // =============================================================
        //  Langkah 6–8: DASAR PAJAK, PAJAK, TOTAL AKHIR
        //  Promo tidak dikurangkan dari dasar pajak.
        // =============================================================
        long dasarPajak = subtotalAwal - nominalDiskon;
        if (dasarPajak < 0L) dasarPajak = 0L;

        long nominalPajak = Math.round(dasarPajak * TARIF_PAJAK);
        long totalAkhir   = dasarPajak + nominalPajak + BIAYA_PELAYANAN;

        cetakStruk(
            pesananMenu, pesananQty,
            sub0, sub1, sub2, sub3,
            subtotalAwal,
            promoAktif, unitGratis, nilaiManfaatPromo,
            diskonAktif, nominalDiskon,
            dasarPajak, nominalPajak, totalAkhir
        );
    }


    // =================================================================
    //  METHOD 7 — cetakStruk(...)
    // =================================================================
    static void cetakStruk(
            Menu[] pesananMenu, int[] pesananQty,
            long sub0, long sub1, long sub2, long sub3,
            long subtotalAwal,
            boolean promoAktif, int unitGratis, long nilaiManfaatPromo,
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

        cetakItemStruk(pesananMenu[0], pesananQty[0], sub0);
        cetakItemStruk(pesananMenu[1], pesananQty[1], sub1);
        cetakItemStruk(pesananMenu[2], pesananQty[2], sub2);
        cetakItemStruk(pesananMenu[3], pesananQty[3], sub3);

        System.out.println(S);
        System.out.printf("  %-36s %s%n", "Subtotal Awal", formatRupiah(subtotalAwal));

        if (promoAktif) {
            System.out.printf("  %-36s %s%n",
                    "Promo B1G1 Es Teh Manis", "Berlaku");
            System.out.printf("    Dapatkan %d unit gratis (nilai manfaat: %s)%n",
                    unitGratis, formatRupiah(nilaiManfaatPromo));
        } else {
            System.out.printf("  %-36s %s%n",
                    "Promo B1G1 Es Teh Manis", "Tidak berlaku");
        }

        if (diskonAktif) {
            System.out.printf("  %-36s %s%n",
                    "Diskon 10%", "-" + formatRupiah(nominalDiskon));
        } else {
            System.out.printf("  %-36s %s%n",
                    "Diskon 10%", "Tidak berlaku");
        }

        System.out.println(S);
        System.out.printf("  %-36s %s%n", "Dasar Pengenaan Pajak", formatRupiah(dasarPajak));
        System.out.printf("  %-36s %s%n", "Pajak (10%)",            formatRupiah(nominalPajak));
        System.out.printf("  %-36s %s%n", "Biaya Pelayanan",        formatRupiah(BIAYA_PELAYANAN));
        System.out.println(L);
        System.out.printf("  %-36s %s%n", "TOTAL PEMBAYARAN",       formatRupiah(totalAkhir));
        System.out.println(L);
        System.out.println("         Terima kasih telah berkunjung!");
        System.out.println("          Selamat menikmati hidangan. :)");
        System.out.println(L);
    }


    // =================================================================
    //  METHOD 8 — cetakItemStruk(Menu, int, long)
    //
    //  switch-case (indikator 3) untuk label kategori.
    // =================================================================
    static void cetakItemStruk(Menu menu, int qty, long subtotal) {
        if (menu == null) return;

        String labelKategori;
        switch (menu.getKategori()) {
            case "Makanan":  labelKategori = "Makanan"; break;
            case "Minuman":  labelKategori = "Minuman"; break;
            default:         labelKategori = "Lainnya";
        }

        System.out.printf("  %-22s [%-7s]  %s%n",
                menu.getNama(), labelKategori, formatRupiah(subtotal));
        System.out.printf("     %d x %s/porsi%n",
                qty, formatRupiah(menu.getHarga()));
    }


    // =================================================================
    //  METHOD HELPER — formatRupiah(long)
    //
    //  Locale.forLanguageTag("id-ID") — tidak deprecated.
    //  25000 → "Rp25.000"
    // =================================================================
    static String formatRupiah(long jumlah) {
        NumberFormat nf = NumberFormat.getInstance(Locale.forLanguageTag("id-ID"));
        nf.setGroupingUsed(true);
        return "Rp" + nf.format(jumlah);
    }
}
