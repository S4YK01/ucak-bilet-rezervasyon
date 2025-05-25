import java.util.*;
import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

class Ucak {
    String model;
    String marka;
    String seriNo;
    int kapasite;

    public Ucak(String model, String marka, String seriNo, int kapasite) {
        this.model = model;
        this.marka = marka;
        this.seriNo = seriNo;
        this.kapasite = kapasite;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("model", model);
        obj.put("marka", marka);
        obj.put("seriNo", seriNo);
        obj.put("kapasite", kapasite);
        return obj;
    }
}

class Lokasyon {
    String ulke;
    String sehir;
    String havaalani;
    boolean aktif;

    public Lokasyon(String ulke, String sehir, String havaalani, boolean aktif) {
        this.ulke = ulke;
        this.sehir = sehir;
        this.havaalani = havaalani;
        this.aktif = aktif;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("ulke", ulke);
        obj.put("sehir", sehir);
        obj.put("havaalani", havaalani);
        obj.put("aktif", aktif);
        return obj;
    }
}

class Ucus {
    Lokasyon kalkis;
    Lokasyon varis;
    String saat;
    Ucak ucak;

    public Ucus(Lokasyon kalkis, Lokasyon varis, String saat, Ucak ucak) {
        this.kalkis = kalkis;
        this.varis = varis;
        this.saat = saat;
        this.ucak = ucak;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ucus)) return false;
        Ucus other = (Ucus) o;
        return kalkis.equals(other.kalkis) &&
               varis.equals(other.varis) &&
               saat.equals(other.saat) &&
               ucak.seriNo.equals(other.ucak.seriNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kalkis, varis, saat, ucak.seriNo);
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("kalkis", kalkis.toJSON());
        obj.put("varis", varis.toJSON());
        obj.put("saat", saat);
        obj.put("ucak", ucak.toJSON());
        return obj;
    }
}

class Rezervasyon {
    Ucus ucus;
    String ad;
    String soyad;
    int yas;

    public Rezervasyon(Ucus ucus, String ad, String soyad, int yas) {
        this.ucus = ucus;
        this.ad = ad;
        this.soyad = soyad;
        this.yas = yas;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("ucus", ucus.toJSON());
        obj.put("ad", ad);
        obj.put("soyad", soyad);
        obj.put("yas", yas);
        return obj;
    }
}

public class Main {
    static List<Ucus> ucuslar = new ArrayList<>();
    static List<Rezervasyon> rezervasyonlar = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Ucak u1 = new Ucak("A320", "Airbus", "SN123", 2);
        Lokasyon l1 = new Lokasyon("Türkiye", "İstanbul", "IST", true);
        Lokasyon l2 = new Lokasyon("Almanya", "Berlin", "BER", true);
        Ucus ucs1 = new Ucus(l1, l2, "12:00", u1);
        ucuslar.add(ucs1);

        System.out.println("Mevcut Uçuşlar:");
        for (int i = 0; i < ucuslar.size(); i++) {
            Ucus u = ucuslar.get(i);
            System.out.println(i + ". " + u.kalkis.sehir + " -> " + u.varis.sehir + " - Saat: " + u.saat);
        }

        int secim = -1;
        while (true) {
            System.out.print("Seçmek istediğiniz uçuş numarası: ");
            try {
                secim = Integer.parseInt(sc.nextLine());
                if (secim >= 0 && secim < ucuslar.size()) break;
                else System.out.println("Geçerli bir numara giriniz.");
            } catch (Exception e) {
                System.out.println("Lütfen sayı giriniz.");
            }
        }

        Ucus secilen = ucuslar.get(secim);

        long count = rezervasyonlar.stream()
                        .filter(r -> r.ucus.equals(secilen))
                        .count();

        if (count >= secilen.ucak.kapasite) {
            System.out.println("Bu uçuşta boş koltuk yok!");
            return;
        }

        System.out.print("Adınız: ");
        String ad = sc.nextLine();
        System.out.print("Soyadınız: ");
        String soyad = sc.nextLine();

        int yas = -1;
        while (true) {
            System.out.print("Yaşınız: ");
            try {
                yas = Integer.parseInt(sc.nextLine());
                if (yas > 0) break;
                else System.out.println("Geçerli bir yaş giriniz.");
            } catch (Exception e) {
                System.out.println("Lütfen sayı giriniz.");
            }
        }

        Rezervasyon rez = new Rezervasyon(secilen, ad, soyad, yas);
        rezervasyonlar.add(rez);

        System.out.println("Rezervasyon başarıyla oluşturuldu!");
        kaydetJSON("rezervasyonlar.json", rezervasyonlar);
    }

    public static void kaydetJSON(String dosyaAdi, List<Rezervasyon> rezervasyonlar) {
        JSONArray arr = new JSONArray();
        for (Rezervasyon r : rezervasyonlar) {
            arr.add(r.toJSON());
        }

        try (FileWriter file = new FileWriter(dosyaAdi)) {
            file.write(arr.toJSONString());
            file.flush();
            System.out.println("Rezervasyonlar kaydedildi: " + dosyaAdi);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
