# Sổ Tài Chính — Android
Mã nguồn Android Native dùng Kotlin + Jetpack Compose.

- Package: `vn.tung.taichinh`
- AGP 8.7.3 / Kotlin 2.0.21 / Gradle 8.9
- SDK 35 / Min SDK 26 / JVM 17
- Lưu trữ SQLite cục bộ, hoạt động offline.

## Đã có trong bản khởi đầu
- Màn hình Tài khoản của tôi, nhóm tài khoản, tổng tài sản và thêm tài khoản.
- Màn hình Ghi chép thu/chi theo ảnh tham chiếu: số tiền, hạng mục, tài khoản, ngày, diễn giải, đối tượng, địa điểm, phí, loại trừ báo cáo.
- Lưu giao dịch và cập nhật số dư.
- Tổng quan và báo cáo thu chi cơ bản.
- Thanh điều hướng 5 mục.

## Chưa hoàn thiện
Các phân hệ vay nợ, tiết kiệm, ngân sách, kế hoạch, nhóm, định kỳ, nhắc nhở; báo cáo tùy chỉnh; nhập/xuất XLSX/PDF/CSV/JSON; Excel mẫu riêng; sao lưu/khôi phục; sửa/xóa giao dịch và kiểm thử nâng cao.

## Biên dịch
Cần JDK 17 và Android SDK 35:
```bash
gradle wrapper --gradle-version 8.9
./gradlew assembleDebug
```
APK: `app/build/outputs/apk/debug/app-debug.apk`

## Đưa lên GitHub
Tạo repository trống, giải nén ZIP, tải toàn bộ nội dung dự án lên repository rồi commit. Hoặc dùng:
```bash
git init
git add .
git commit -m "Initial Sổ Tài Chính Android project"
git branch -M main
git remote add origin https://github.com/USERNAME/SoTaiChinh-Android.git
git push -u origin main
```
Thay USERNAME bằng tài khoản GitHub của bạn.

## Mở bằng Code on the Go
Mở thư mục gốc chứa `settings.gradle.kts`, tạo Gradle Wrapper nếu chưa có, sau đó chạy `./gradlew assembleDebug`.
Lưu ý: dữ liệu nằm trong vùng riêng của ứng dụng; gỡ app có thể xóa dữ liệu. Chưa nên dùng cho dữ liệu quan trọng trước khi có backup/restore.
