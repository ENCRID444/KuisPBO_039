package com.mycompany.kuispbo_039;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SistemRumahSakit {
    private String namaRS;
    private HashMap<String, Pasien> mapPasien;
    private HashMap<String, LayananMedis> mapLayanan;
    private ArrayList<Transaksi> listTransaksi;

    public SistemRumahSakit(String namaRS) {
        this.namaRS = namaRS;
        this.mapPasien = new HashMap<>();
        this.mapLayanan = new HashMap<>();
        this.listTransaksi = new ArrayList<>();
    }

    public void tambahPasien(String id, String nama) {
        mapPasien.put(id, new Pasien(id, nama));
    }

    public void tambahLayananMedis(String kode, String nama, int kuota) {
        mapLayanan.put(kode, new LayananMedis(kode, nama, kuota));
    }

    public void jalankanTransaksiDariFile(String namaFile) throws Exception {
        File file = new File(namaFile);
        if (!file.exists()) {
            throw new Exception("File " + namaFile + " tidak ditemukan di dalam project!");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String baris;
            while ((baris = br.readLine()) != null) {
                baris = baris.trim();
                if (baris.isEmpty()) continue;

                try {
                    String[] bagian = baris.split(";");
                    String idPasien = "";
                    String kodeLayanan = "";
                    int jumlah = 0;

                    for (String str : bagian) {
                        String[] pasanganKeyValue = str.split(":");
                        if (pasanganKeyValue.length == 2) {
                            String key = pasanganKeyValue[0].trim();
                            String value = pasanganKeyValue[1].trim();

                            if (key.equals("ID")) idPasien = value;
                            else if (key.equals("KD")) kodeLayanan = value;
                            else if (key.equals("JML")) jumlah = Integer.parseInt(value);
                        }
                    }

                    Pasien p = mapPasien.get(idPasien);
                    if (p == null) throw new TransaksiException("Pasien dengan ID '" + idPasien + "' tidak terdaftar.");

                    LayananMedis lm = mapLayanan.get(kodeLayanan);
                    if (lm == null) throw new TransaksiException("Layanan dengan Kode '" + kodeLayanan + "' tidak tersedia.");

                    lm.kurangiKuota(jumlah);

                    listTransaksi.add(new Transaksi(p, lm, jumlah));
                    System.out.println("[SUKSES] " + p.getNama() + " memesan " + jumlah + " " + lm.getNama());

                } catch (TransaksiException e) {
                    System.out.println("[GAGAL] Baris '" + baris + "' -> " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("[ERROR PARSING] Format data salah pada baris: " + baris);
                }
            }
        }
    }

    public void simpanLaporanAkhir() {
        System.out.println("\n--- MEMULAI PENYIMPANAN LAPORAN ---");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("state.bak"))) {
            oos.writeObject(listTransaksi);
            System.out.println("[INFO] Data transaksi berhasil diserialisasi ke 'state.bak'.");
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal membuat file state.bak: " + e.getMessage());
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter("laporan.txt"))) {
            pw.println("=========================================================================");
            pw.println(String.format("| %-10s | %-15s | %-25s | %-5s |", "ID PASIEN", "NAMA PASIEN", "LAYANAN MEDIS", "JML"));
            pw.println("=========================================================================");
            
            for (Transaksi t : listTransaksi) {
                pw.println(String.format("| %-10s | %-15s | %-25s | %-5d |", 
                        t.getPasien().getId(), 
                        t.getPasien().getNama(), 
                        t.getLayanan().getNama(), 
                        t.getJumlah()));
            }
            pw.println("=========================================================================");
            System.out.println("[INFO] Laporan akhir berhasil dicetak ke 'laporan.txt' dalam format tabel.");
            
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal membuat file laporan.txt: " + e.getMessage());
        }
    }
}