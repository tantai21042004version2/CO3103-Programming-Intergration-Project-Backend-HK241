# Sử dụng một image có chứa JDK và Maven để build và chạy ứng dụng
FROM maven:3.8.8-eclipse-temurin-17 AS build

# Tạo thư mục chứa ứng dụng trong container
WORKDIR /app

# Copy toàn bộ mã nguồn vào container
COPY . .

# Build dự án Maven, tạo file jar
RUN mvn clean package -DskipTests

# Sử dụng một image nhỏ hơn để chạy ứng dụng (sau khi đã build)
FROM openjdk:17-jdk-alpine

# Thiết lập biến môi trường để container nhận diện đúng timezone (tùy chọn)
ENV TZ=Asia/Ho_Chi_Minh

# Tạo thư mục chứa ứng dụng trong container
WORKDIR /app

# Copy file jar từ giai đoạn build vào container
COPY --from=build /app/target/music-backend-0.0.1-SNAPSHOT.jar app.jar

# Chỉ định cổng chạy cho container
EXPOSE 8088

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
