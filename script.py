from flask import Flask, request, jsonify
import subprocess

app = Flask(__name__)

@app.route("/deploy", methods=["POST"])
def deploy():
    data = request.json
    print("🚀 Recibido deploy:", data)

    try:
        # Hardcoded credentials (⚠️ inseguro)
        docker_user = "walterbaya"
        docker_pass = "Alfiosos17$"

        # Login
        login = subprocess.run(
            ["docker", "login", "-u", docker_user, "--password-stdin"],
            input=docker_pass, text=True, check=True, capture_output=True
        )
        print(login.stdout)

        # Pull de la última imagen
        result = subprocess.run(
            ["docker-compose", "pull"], check=True, capture_output=True, text=True
        )
        print(result.stdout)

        # Levantar contenedores
        result = subprocess.run(
            ["docker-compose", "up", "-d", "--build"], check=True, capture_output=True, text=True
        )
        print(result.stdout)

        return jsonify({"status": "ok"})
    except subprocess.CalledProcessError as e:
        print("❌ Error:", e.stderr)
        return jsonify({"status": "error", "message": e.stderr}), 500


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=4000)

