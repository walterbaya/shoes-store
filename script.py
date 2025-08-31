from flask import Flask, request, jsonify
import subprocess
import threading
import os
import signal
import yaml

app = Flask(__name__)

def get_ports_from_compose(file_path="docker-compose.yml"):
    """Extrae todos los puertos mapeados en docker-compose.yml"""
    ports = []
    with open(file_path, "r") as f:
        compose = yaml.safe_load(f)
        for service in compose.get("services", {}).values():
            for port in service.get("ports", []):
                local_port = port.split(":")[0]  # toma la parte antes de ":"
                try:
                    ports.append(int(local_port))
                except ValueError:
                    pass
    return ports

def free_ports(ports):
    """Mata procesos que estén usando los puertos especificados"""
    for port in ports:
        try:
            if os.name == "nt":  # Windows
                result = subprocess.run(
                    f'netstat -ano | findstr :{port}', shell=True, capture_output=True, text=True
                )
                lines = result.stdout.strip().splitlines()
                for line in lines:
                    pid = line.split()[-1]
                    print(f"🛑 Matando proceso PID {pid} que usa el puerto {port}")
                    subprocess.run(f"taskkill /PID {pid} /F", shell=True)
            else:  # Linux / macOS
                result = subprocess.run(
                    f"lsof -ti:{port}", shell=True, capture_output=True, text=True
                )
                pids = result.stdout.strip().splitlines()
                for pid in pids:
                    print(f"🛑 Matando proceso PID {pid} que usa el puerto {port}")
                    os.kill(int(pid), signal.SIGKILL)
        except Exception as e:
            print(f"⚠️ No se pudo liberar el puerto {port}: {e}")

def run_deploy():
    try:
        docker_user = "walterbaya"
        docker_pass = "Alfiosos17$"

        # Liberar puertos usados por docker-compose
        ports = get_ports_from_compose("docker-compose.yml")
        free_ports(ports)

        # Login a Docker
        login = subprocess.run(
            ["docker", "login", "-u", docker_user, "--password-stdin"],
            input=docker_pass, text=True, check=True, capture_output=True
        )
        print(login.stdout)

        # Pull de las últimas imágenes
        pull = subprocess.run(
            ["docker-compose", "pull"],
            check=True, capture_output=True, text=True
        )
        print(pull.stdout)

        # Levantar contenedores en background
        up = subprocess.run(
            ["docker-compose", "up", "-d", "--build"],
            check=True, capture_output=True, text=True
        )
        print(up.stdout)

        # Logs en tiempo real
        logs = subprocess.Popen(
            ["docker-compose", "logs", "-f"],
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True
        )
        for line in logs.stdout:
            print(line, end="")

    except subprocess.CalledProcessError as e:
        print("❌ Error:", e.stderr)

@app.route("/", methods=["POST"])
def deploy():
    data = request.json
    print("🚀 Recibido deploy:", data)

    # Ejecutar deploy en un thread para no bloquear Flask
    threading.Thread(target=run_deploy).start()

    return jsonify({"status": "accepted"}), 202

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=4000)

