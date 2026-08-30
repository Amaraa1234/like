# 🎡 Асуултын Хүрд (Quiz Wheel Game)

JavaFX болон MySQL ашиглан хийсэн интерактив асуулт хариултын, хүрдтэй ширээний (Desktop) тоглоом.

## 🚀 Ашигласан технологиуд

* **Хэл:** Java 17+
* **GUI Framework:** JavaFX 21
* **Мэдээллийн сан:** MySQL 8.0+
* **Connection Pool:** HikariCP
* **Нууцлал:** BCrypt Password Hashing (`jbcrypt`)
* **Лог хөтлөлт:** SLF4J / Logback
* **Төслийн цуглуулагч:** Maven

---

## 🛠️ Мэдээллийн баазын тохиргоо (Database Setup)

Тоглоомыг ажиллуулахаас өмнө MySQL бааз дээрээ дараах скриптийг ажиллуулж бааз болон хүснэгтүүдийг үүсгэнэ.

```sql
CREATE DATABASE IF NOT EXISTS game_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE game_db;

-- Хэрэглэгчийн хүснэгт
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    score INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Асуултын хүснэгт
CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_option ENUM('A', 'B', 'C', 'D') NOT NULL
);

-- Жишээ асуултууд нэмэх
INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option) VALUES
('Монгол улсын нийслэл ямар хот вэ?', 'Дархан', 'Улаанбаатар', 'Эрдэнэт', 'Чойбалсан', 'B'),
('Java хэлийг анх ямар компани хөгжүүлсэн бэ?', 'Microsoft', 'Apple', 'Sun Microsystems', 'Google', 'C'),
('Дэлхийн хамгийн том далай юу вэ?', 'Атлантын далай', 'Энэтхэгийн далай', 'Номхон далай', 'Умард мөсөн далай', 'C');