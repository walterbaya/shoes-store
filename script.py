from flask import Flask, request, jsonify
import subprocess

app = Flask(__name__)

@app.route("/", methods=["POST"])
def deploy():
    data = request.json
    print("🚀 Recibido deploy:", data)

    try:
        # Traer última imagen y levantar contenedores
        result = subprocess.run(
            ["docker-compose", "pull"], check=True, capture_output=True, text=True
        )
        print(result.stdout)

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
