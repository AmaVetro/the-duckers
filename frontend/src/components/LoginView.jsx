//frontend/src/components/LoginView.jsx:


import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export const LoginView = () => {

  const { login } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();



  const onSubmit = async (e) => {

    e.preventDefault();

    try {

      await login(email, password);

      navigate("/account");

    } catch (error) {

      alert("Login failed. Please check your credentials.");

      console.error(error);

    }

  };



  const canSubmit = email.trim().length > 0 && password.trim().length > 0;



  return (

    <div className="container d-flex align-items-center justify-content-center" style={{ minHeight: "75vh" }}>

      <div className="card shadow-sm" style={{ width: "100%", maxWidth: 440 }}>

        <div className="card-body p-4">

          <h4 className="mb-1 text-center">Iniciar sesión</h4>



          <form onSubmit={onSubmit}>

            <div className="mb-3">

              <label className="form-label">Correo electrónico</label>

              <input
                type="email"
                className="form-control form-control-lg"
                placeholder="tu@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                autoComplete="email"
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

              <button
                className="btn btn-primary btn-lg"
                type="submit"
                disabled={!canSubmit}
              >
                Entrar
              </button>

            </div>

          </form>



          <p className="text-center text-gray-200 mt-3 mb-0">
            ¿No tienes una cuenta? <span className="fw-semibold text-white">Regístrate</span>
          </p>

        </div>

      </div>

    </div>

  );

};