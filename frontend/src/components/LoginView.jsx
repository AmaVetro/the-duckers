//FrontEndDuckers/src/components/LoginView.jsx:

import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export const LoginView = () => {
  const { login } = useAuth();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState(""); 
  const navigate = useNavigate();

  const onSubmit = (e) => {
    e.preventDefault();
    if (!username.trim() || !password.trim()) return; 
    login({ username: username || "demo_user" }); 
    navigate("/account");
  };

  const canSubmit = username.trim().length > 0 && password.trim().length > 0;

  return (
    <div className="container d-flex align-items-center justify-content-center" style={{ minHeight: "75vh" }}>
      <div className="card shadow-sm" style={{ width: "100%", maxWidth: 440 }}>
        <div className="card-body p-4">
          <h4 className="mb-1 text-center">Iniciar sesión</h4>


          <form onSubmit={onSubmit}>
            <div className="mb-3">
              <label className="form-label">Nombre de usuario</label>
              <input
                className="form-control form-control-lg"
                placeholder="tu_usuario"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                autoComplete="username"
              />
            </div>

            <div className="mb-2">
              <label className="form-label">Contraseña</label>
              <input
                type="password"
                className="form-control form-control-lg"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="current-password"
              />
            </div>

            <div className="d-grid mt-3">
              <button className="btn btn-primary btn-lg" type="submit" disabled={!canSubmit}>
                Entrar
              </button>
            </div>
          </form>

          <p className="text-center text-gray-200 mt-3 mb-0">
            ¿No tienes una cuenta? <span className="fw-semibold text-white">Regístrate</span>
          </p>
          <div className="d-flex align-items-center my-3">
            <hr className="flex-grow-1" />
            <span className="mx-2 text-muted">o</span>
            <hr className="flex-grow-1" />
          </div>

          <div className="d-grid gap-2">
            <button className="btn btn-outline-danger btn-lg" type="button" onClick={() => alert("Demo: Google")}>
              🚀 Iniciar con Google
            </button>
            <button className="btn btn-outline-primary btn-lg" type="button" onClick={() => alert("Demo: Facebook")}>
              👍 Iniciar con Facebook
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
