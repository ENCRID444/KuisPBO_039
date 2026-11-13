package com.mycompany.kuispbo_039;

public class Pasien extends Entitas {
    private static final long serialVersionUID = 1L;

    public Pasien(String id, String nama) {
        super(id, nama);
    }

    @Override
    public String getInfoDetail() {
        return "Pasien [" + getId() + "] - " + getNama();
    }
}