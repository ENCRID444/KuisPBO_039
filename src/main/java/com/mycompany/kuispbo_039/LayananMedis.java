package com.mycompany.kuispbo_039;

public class LayananMedis extends Entitas {
    private static final long serialVersionUID = 1L;
    private int kuota;

    public LayananMedis(String kode, String namaLayanan, int kuota) {
        super(kode, namaLayanan);
        this.kuota = kuota;
    }

    public int getKuota() {
        return kuota;
    }

    public void kurangiKuota(int jumlah) throws TransaksiException {
        if (jumlah <= 0) {
            throw new TransaksiException("Jumlah permintaan harus lebih dari 0.");
        }
        if (this.kuota < jumlah) {
            throw new TransaksiException("Kuota tidak mencukupi untuk " + getNama() + " (Sisa: " + this.kuota + ", Diminta: " + jumlah + ")");
        }
        this.kuota -= jumlah;
    }

    @Override
    public String getInfoDetail() {
        return "Layanan [" + getId() + "] - " + getNama() + " (Sisa Kuota: " + kuota + ")";
    }
}