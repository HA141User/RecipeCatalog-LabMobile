# Recipe Catalog App 🍳

Aplikasi native Android kelas premium untuk mencari, menjelajahi, dan menyimpan resep makanan dari seluruh dunia. Aplikasi ini dikembangkan sebagai pemenuhan Tugas Final Lab Mobile 2026.

## 👨‍💻 Informasi Developer
- **Nama:** Muhammad Hairi
- **NIM:** H071241055
- **Program Studi:** Sistem Informasi, Universitas Hasanuddin

## ✨ Fitur Utama (Cara Penggunaan)
1. **Eksplorasi Resep (Home):** Saat aplikasi dibuka, pengguna akan disambut dengan daftar resep pilihan. Pengguna bisa mencari resep spesifik menggunakan kolom pencarian, atau menggunakan *chip filter* cepat (Ayam, Sapi).
2. **Detail Resep Lengkap:** Klik pada salah satu kartu resep untuk melihat takaran bahan (ingredients) dan instruksi memasak (instructions) secara mendetail.
3. **Simpan Favorit (Offline Support):** Klik ikon bintang pada kartu resep atau di halaman detail untuk menyimpannya. Resep favorit dapat diakses kapan saja di tab "Favorites" meskipun perangkat sedang tidak terhubung ke internet.
4. **Adaptive Day/Night Theme:** Klik ikon Matahari/Bulan di sudut kanan atas untuk mengganti tema aplikasi secara instan. Pilihan tema akan tersimpan secara permanen.
5. **Swipe to Refresh:** Usap layar ke bawah pada halaman utama untuk memuat ulang daftar resep secara acak.

## 🛠️ Implementasi Teknis (Tech Stack)
Aplikasi ini dibangun menggunakan arsitektur modern Android Native (Java) yang memenuhi seluruh spesifikasi teknis lab:
- **Networking:** Menggunakan **Retrofit2** untuk melakukan *fetching* data JSON dari public API (TheMealDB).
- **Local Persistence:** Menggunakan **SQLite** (`DatabaseHelper`) untuk manajemen operasi CRUD data resep favorit, serta **SharedPreferences** untuk menyimpan *state* Dark Mode.
- **Background Processing:** Menerapkan `ExecutorService` dan `Handler` (Looper UI) untuk memastikan operasi *database* berjalan mulus di latar belakang tanpa memblokir *Main Thread*.
- **UI & Navigation:** Dibangun dengan *Material Design Components* (MaterialCardView, BottomNavigationView), `RecyclerView` untuk *list rendering*, serta *Navigation Component* untuk perpindahan antar *Fragment*.
- **Image Loader:** Menggunakan **Glide** untuk memuat gambar *thumbnail* dari *server* dengan teknik *caching*.

## 🚀 Cara Instalasi untuk Tester
Bagi teman-teman yang ingin mencoba aplikasi ini langsung di HP Android:
1. Pergi ke bagian **[Releases]** di sebelah kanan halaman GitHub ini.
2. Unduh file `RecipeCatalog_Release.apk`.
3. Buka file APK tersebut di HP Android Anda (pastikan pengaturan "Install from Unknown Sources" diaktifkan jika diminta).
4. Selamat memasak!