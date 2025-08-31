from flask import Flask, request, jsonify
import subprocess
import threading

app = Flask(__name__)

def run_deploy():
    try:
        docker_user = "walterbaya"
        docker_pass = "Alfiosos17$"

        # Login a Docker
        login = subprocess.run(
            ["docker", "login", "-u", docker_user, "--password-stdin"],
            input=docker_pass, text=True, check=True, capture_output=True
        )
        print(login.stdout)

        # Pull de la última imagen
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

        # Seguir los logs en tiempo real
        logs = subprocess.Popen(
            ["docker-compose", "logs", "-f"],
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True
        )

        for line in logs.stdout:
            print(line, end="")  # imprime cada línea en consola

    except subprocess.CalledProcessError as e:
        print("❌ Error:", e.stderr)

@app.route("/", methods=["POST"])
def deploy():
    data = request.json
    print("🚀 Recibido deploy:", data)

    # Ejecutar deploy en un thread para no bloquear Flask
    threading.Thread(target=run_deploy).start()

    # Responder rápido al webhook
    return jsonify({"status": "accepted"}), 202

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=4000)

