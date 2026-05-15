[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]

<br />
<div align="center">
  <a href="#">
    <img src="https://github.com/ATHENA-Malware-APK-Detection-System/Dataset-Generation/blob/a90692ab1f09ce3262ef63effd41ea68b5eeb319/Logo.png" alt="Logo" width="300" height="300">
  </a>

  <h3 align="center">Dataset Generation - APK to Image</h3>

  <p align="center">
    Aplikasi Client-Side untuk sistem deteksi malware ATHENA yang terhubung dengan layanan Cloud Inference.
  </p>
</div>

---

## About Front-End App

Aplikasi ini berfungsi sebagai **Frontend Interface** untuk sistem deteksi malware ATHENA. Berbeda dengan deteksi konvensional, aplikasi ini mengirimkan data aplikasi ke server (Cloud) untuk diproses menjadi citra dan dianalisis menggunakan model Deep Learning yang berjalan di sisi server.

Hal ini memastikan pemindaian tetap akurat tanpa membebani performa baterai dan memori perangkat pengguna.

---

### Key Features

* **APK Uploader:** Mengunggah file APK yang dipilih pengguna dari perangkat ke server.
* **Cloud-Inference Status:** Menampilkan progress pemrosesan (Uploading, Processing, Analyzing).
* **Detailed Security Report:** Menerima dan menampilkan hasil klasifikasi (Malware vs Benign) serta skor kepercayaan (confidence score) dari Cloud.

---

### How It Works (System Architecture)

Aplikasi ini bekerja dalam siklus **Client-Server**:

1. **Upload:** User memilih APK di aplikasi Android.
2. **Transfer:** Aplikasi mengirimkan file melalui protokol REST API/Secure Connection.
3. **Cloud Processing:** Server melakukan ekstraksi (Manifest, DEX, ARSC) dan konversi citra.
4. **Inference:** Model Deep Learning di Cloud melakukan klasifikasi.
5. **Result:** Hasil dikirimkan kembali ke aplikasi Android untuk ditampilkan.

---

## Contact

Email : [ummuathiyyah05@gmail.com](mailto:ummuathiyyah05@gmail.com)<br>
LinkedIn :
[Ummu Athiya](https://www.linkedin.com/in/ummu-athiya-833b541b7/)

[contributors-shield]: https://img.shields.io/github/contributors/ATHENA-Malware-APK-Detection-System/Front-End.svg?style=for-the-badge
[contributors-url]: https://github.com/ATHENA-Malware-APK-Detection-System/Front-End/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/ATHENA-Malware-APK-Detection-System/Front-End.svg?style=for-the-badge
[forks-url]: https://github.com/ATHENA-Malware-APK-Detection-System/Front-End/network/members
[stars-shield]: https://img.shields.io/github/stars/ATHENA-Malware-APK-Detection-System/Front-End.svg?style=for-the-badge
[stars-url]: https://github.com/ATHENA-Malware-APK-Detection-System/Front-End/stargazers
[issues-shield]: https://img.shields.io/github/issues/ATHENA-Malware-APK-Detection-System/Front-End.svg?style=for-the-badge
[issues-url]: https://github.com/ATHENA-Malware-APK-Detection-System/Front-End/issues
[license-shield]: https://img.shields.io/github/license/ATHENA-Malware-APK-Detection-System/Front-End.svg?style=for-the-badge
[license-url]: https://github.com/ATHENA-Malware-APK-Detection-System/Front-End/blob/main/LICENSE
