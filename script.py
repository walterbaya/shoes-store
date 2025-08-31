from flask import Flask, request, jsonify
import subprocess
import threading

app = Flask(__name__)

def run_deploy():
    try:
        docker_user = "walterbaya"
        docker_pass = "Alfiosos17$"

        # Login
        login = subprocess.run(
            ["docker", "login", "-u", docker_user, "--password-stdin"],
            input=docker_pass, text=True, check=True, capture_output=True
        )
        print(login.stdout)

        # Pull de la última imagen
        result = subprocess.run(["docker-compose", "pull"], check=True, capture_output=True, text=True)
        print(result.stdout)

        # Levantar contenedores
        result = subprocess.run(["docker-compose", "up", "-d", "--build"], check=True, capture_output=True, text=True)
        print(result.stdout)

    except subprocess.CalledProcessError as e:
        print("❌ Error:", e.stderr)

@app.route("/", methods=["POST"])
def deploy():
    data = request.json
    print("🚀 Recibido deploy:", data)

    # Correr deploy en un thread separado (no bloquea Flask)
    threading.Thread(target=run_deploy).start()

    # Responder rápido al webhook
    return jsonify({"status": "accepted"}), 202

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=4000)


