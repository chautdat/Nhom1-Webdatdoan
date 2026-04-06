# Nhom1 Web Đặt Đồ Ăn
---
## Giới thiệu
**Nhom1 Web Đặt Đồ Ăn** là hệ thống đặt món ăn trực tuyến giúp người dùng xem thực đơn, thêm món vào giỏ hàng, đặt hàng, quản lý địa chỉ và theo dõi đơn hàng.

Hệ thống được thiết kế theo hướng **Web Service (RESTful API)**, kết hợp frontend hiện đại, giúp:
- Quản lý thông tin người dùng
- Quản lý danh mục và món ăn
- Quản lý giỏ hàng và đơn hàng
- Hỗ trợ mở rộng thành hệ thống thương mại điện tử cho nhà hàng

---
## Mục đích dự án
Nhom1 Web Đặt Đồ Ăn được xây dựng nhằm:
- Kết nối nhanh chóng giữa khách hàng và nhà hàng
- Tối ưu trải nghiệm đặt món và quản lý đơn hàng
- Quản lý dữ liệu một cách rõ ràng, có hệ thống

### Định hướng phát triển
- Thanh toán online
- Chat giữa người dùng và nhà hàng
- Đánh giá / review món ăn
- Thông báo realtime cho đơn hàng
- Mở rộng hệ thống thống kê và quản trị

---
## Công nghệ sử dụng

### Ngôn ngữ & Framework
- **Java 21**
  - Phiên bản LTS, hiệu năng cao, hỗ trợ tính năng hiện đại

- **Spring Boot 4.0.5**
  - Framework chính để xây dựng backend
  - Tích hợp:
    - Web
    - Security
    - Data JPA
    - Validation
    - WebSocket

### Web & API
- **Spring Web MVC**
  - Xây dựng RESTful API
  - Xử lý HTTP request/response
  - Mapping endpoint bằng `@RestController`

- **Spring WebSocket**
  - Hỗ trợ cập nhật realtime cho đơn hàng
  - Dùng cho các tính năng thông báo trạng thái

### Database & ORM
- **Spring Data JPA**
  - ORM giúp thao tác database dễ dàng
  - Tự động CRUD qua Repository

- **Hibernate**
  - ORM mặc định của JPA
  - Ánh xạ giữa Entity và bảng dữ liệu

- **MySQL Connector/J**
  - Kết nối hệ thống với MySQL

### Security
- **Spring Security**
  - Bảo mật hệ thống
  - Phân quyền theo vai trò người dùng

- **JWT (JJWT)**
  - Xác thực người dùng bằng token

- **Spring Security Crypto**
  - Mã hóa mật khẩu bằng BCrypt

### Validation
- **Spring Validation**
  - Kiểm tra dữ liệu đầu vào:
    - `@NotNull`
    - `@Email`
    - `@Size`

### Mapping & Code Generation
- **Lombok**
  - Giảm boilerplate code:
    - Getter / Setter
    - Constructor
    - Builder

### Build Tool
- **Maven**
  - Quản lý dependency
  - Build project

- **Maven Wrapper (`mvnw`)**
  - Chạy project không cần cài Maven

### View / Template
- **Thymeleaf**
  - Template engine có sẵn trong project

- **thymeleaf-extras-springsecurity6**
  - Hỗ trợ tích hợp Thymeleaf với Spring Security

### Frontend
- **Vue 3**
  - Framework chính của frontend

- **Vue CLI 5**
  - Công cụ build và chạy frontend

- **Vue Router 4**
  - Điều hướng giữa các trang

- **Vuex 4**
  - Quản lý state toàn cục

- **Axios**
  - Gọi REST API từ backend

- **Vuetify 3**
  - Bộ component UI

- **SweetAlert2**
  - Hiển thị popup thông báo đẹp và rõ ràng

- **Chart.js / vue-chartjs**
  - Hiển thị biểu đồ thống kê

- **@stomp/stompjs**
  - Giao tiếp WebSocket theo chuẩn STOMP

- **SockJS Client**
  - Fallback cho WebSocket

- **Sass / sass-loader**
  - Hỗ trợ viết CSS linh hoạt hơn

- **ESLint**
  - Kiểm tra và chuẩn hóa code frontend

### Hạ tầng & Khác
- **MySQL**
  - Hệ quản trị cơ sở dữ liệu của hệ thống

- **CORS**
  - Cho phép frontend và backend giao tiếp trong môi trường phát triển

- **Image Upload / Static Files**
  - Ảnh món ăn và danh mục được lưu trong thư mục `uploads` và phục vụ qua URL `/uploads/**`

---
## Ghi chú
- Backend và frontend được phát triển tách biệt nhưng cùng nằm trong một repository.
- Hệ thống đang dùng JWT để xác thực và WebSocket để cập nhật realtime.
- Ảnh món ăn và danh mục được upload qua API và hiển thị lại từ thư mục `uploads/`.
