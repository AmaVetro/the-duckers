//frontend/src/components/LoginView.jsx:

import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export const LoginView = () => {

  const { login } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [errors, setErrors] = useState({});

  const navigate = useNavigate();


  const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;


  const validateForm = () => {

    const newErrors = {};

    if (!emailRegex.test(email)) {
      newErrors.email = "Ingresa un correo válido";
    }

    if (password.trim() === "") {
      newErrors.password = "La contraseña es obligatoria";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };



  const onSubmit = async (e) => {

    e.preventDefault();

    const valid = validateForm();

    if (!valid) {
      return;
    }

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

            {/* EMAIL */}

            <div className="mb-3">

              <label className="form-label">Correo electrónico</label>

              <input
                type="email"
                className={`form-control form-control-lg ${email && !emailRegex.test(email) ? "is-invalid" : ""}`}
                placeholder="tu@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                autoComplete="email"
              />

              {errors.email && (
                <div className="text-danger mt-1">
                  {errors.email}
                </div>
              )}

            </div>



            {/* PASSWORD */}

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

              {errors.password && (
                <div className="text-danger mt-1">
                  {errors.password}
                </div>
              )}

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
            ¿No tienes una cuenta?{" "}
            <span
              className="fw-semibold text-white"
              style={{ cursor: "pointer" }}
              onClick={() => navigate("/register")}
            >
              Regístrate
            </span>
          </p>

        </div>

      </div>

    </div>

  );

};