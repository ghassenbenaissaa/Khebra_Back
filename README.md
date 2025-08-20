# KHEBRA_BACK  
*Empowering Seamless Connections for Limitless Innovation*  

![last-commit](https://img.shields.io/github/last-commit/ghassenbenaissaa/Khebra_Back?style=flat&logo=git&logoColor=white&color=0080ff)
![repo-top-language](https://img.shields.io/github/languages/top/ghassenbenaissaa/Khebra_Back?style=flat&color=0080ff)
![repo-language-count](https://img.shields.io/github/languages/count/ghassenbenaissaa/Khebra_Back?style=flat&color=0080ff)

---

## 🔎 Overview
**Khebra_Back** is a backend project built with **Spring Boot**, designed for scalability, security, and real-time applications.  
It includes authentication (JWT), WebSocket communication, media upload with Cloudinary, and modular service layers.  

---

## ✅ Prerequisites
Before installing, make sure you have:
- [Java 17+](https://adoptium.net/)  
- [Maven](https://maven.apache.org/)  
- [Docker](https://www.docker.com/) *(optional)*  

---

## ⚡ Installation  

### 1. Clone the repository  
```sh
git clone https://github.com/ghassenbenaissaa/Khebra_Back
cd Khebra_Back
```

### 2. Build the project  

Using **Maven**:  
```sh
mvn clean package
```

This will generate an executable file in the folder:  
```
target/Khebra_Back-0.0.1-SNAPSHOT.jar
```

---

## ▶️ Run the Application  

### Option 1: Run with Java  
```sh
java -jar target/Khebra_Back-0.0.1-SNAPSHOT.jar
```

### Option 2: Run with Maven  
```sh
mvn spring-boot:run
```

### Option 3: Run with Docker  
```sh
docker build -t khebra_back .
docker run -p 8080:8080 khebra_back
```

---

## 🧪 Testing  
Run the test suite with:  
```sh
mvn test
```

---

## 🌐 API Access
Once the application is running, you can access it at:  
👉 [http://localhost:8080](http://localhost:8080)  

---

⬆️ [Back to top](#khebra_back)
