# ps6_buildathon_preboardcodes

This GitHub repository is for **"AI BUILDATHON 2025 | CrossStack X Zigma Neural"**. The repository contains the solution/code for **Problem Statement 6: "AI Subtitle Generator for Reels"**.

<p>
  <img src="assets/SystemDiagram.jpg" alt="System Diagram" width="600">
</p>

## Tech Stack

The stack is composed of the following components:

### 1. Backend

* **Nginx**
  Acts as a web server and reverse proxy.

* **Java Spring Boot Server**
  Handles authentication, emailing, and user management.

* **Python Flask Server**
  Runs the Whisper model responsible for transcription.

* **MySQL**
  Stores user information along with transcribed files’ SHA-256 hashes and storage locations.

### 2. Port Forwarding

* **Playit**
  Used to expose the Nginx port (8081) to the internet. Playit is required as a TCP tunnel because Airtel does not allow direct port forwarding.

* **Cloudflare Workers**
  Runs a lightweight script to provide a cleaner, user-friendly URL for the Playit tunnel. No processing is performed here.

### 3. Frontend

The frontend uses a combination of **HTML, CSS, and JavaScript** to provide an interactive user interface. Libraries such as **Bootstrap 5** and **DataTables** are used.

## Building & Running (Linux)

### 1. Install requirements

```bash
sudo apt install python3 openjdk-21-jdk python3-pip maven
```

```bash
cd python_flask_ai_server/ && pip install -r requirements.txt
```

### 2. Build the Java application

```bash
cd java_springboot_management_server && mvn clean package
```

### 3. Deploy frontend files

Deploy the contents of the `web_frontend` directory to a web server.

### 4. Run the Python server

```bash
cd python_flask_ai_server && python3 flask_ai_server.py
```

### 5. Run the Java server

Run the generated Spring Boot JAR file from the `target` directory.

## Links

* **Website:** [https://titly.mwnjele.workers.dev/](https://titly.mwnjele.workers.dev/)
* **Project Video:** [https://drive.google.com/file/d/1N70eKwV5mYE1XKIC0iCfB5kNZxZE95yI/view?usp=sharing](https://drive.google.com/file/d/1N70eKwV5mYE1XKIC0iCfB5kNZxZE95yI/view?usp=sharing)

## Notes

This repository may be updated frequently. Expect changes and minimal to no documentation at times.
