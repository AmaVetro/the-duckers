//frontend/src/components/RegisterView.jsx:


import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { register } from "../services/authService";
import { useAuth } from "../hooks/useAuth";

export const RegisterView = () => {

    const navigate = useNavigate();
    const { login } = useAuth();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastNameFather, setLastNameFather] = useState("");
    const [lastNameMother, setLastNameMother] = useState("");
    const [referralCode, setReferralCode] = useState("");

    const [errors, setErrors] = useState({});
    const [showAlert, setShowAlert] = useState(false);

    const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

    const passwordValid =
        password.length >= 8 &&
        /[A-Z]/.test(password) &&
        /[a-z]/.test(password) &&
        /\d/.test(password);

    const emailValid = emailRegex.test(email);

    const formValid =
        emailValid &&
        passwordValid &&
        firstName.trim() !== "" &&
        lastNameFather.trim() !== "" &&
        lastNameMother.trim() !== "";


    const validateForm = () => {

        const newErrors = {};

        if (!emailValid) {
            newErrors.email = "Ingresa un correo válido";
        }

        if (!passwordValid) {
            newErrors.password = "La contraseña no cumple los requisitos";
        }

        if (firstName.trim() === "") {
            newErrors.firstName = "El nombre es obligatorio";
        }

        if (lastNameFather.trim() === "") {
            newErrors.lastNameFather = "El apellido paterno es obligatorio";
        }

        if (lastNameMother.trim() === "") {
            newErrors.lastNameMother = "El apellido materno es obligatorio";
        }

        setErrors(newErrors);

        return Object.keys(newErrors).length === 0;

    };

    const onSubmit = async (e) => {

        e.preventDefault();

        const valid = validateForm();

        if (!valid) {

            setShowAlert(true);

            setTimeout(() => {
                setShowAlert(false);
            }, 5000);

            return;
        }

        try {

            await register({
                email,
                password,
                firstName,
                lastNameFather,
                lastNameMother,
                referralCode
            });

            await login(email, password);

            navigate("/account");

        } catch (error) {

        console.error("Register error:", error);

        if (error.message && error.message.toLowerCase().includes("email")) {

            setErrors((prev) => ({
            ...prev,
            email: "El correo ya está en uso"
            }));

            return;

        }

        alert("No se pudo crear la cuenta.");

        }

    };

    return (

        <div
            className="container d-flex align-items-center justify-content-center"
            style={{ minHeight: "75vh" }}
        >

            <div className="card shadow-sm" style={{ width: "100%", maxWidth: 440 }}>

                <div className="card-body p-4">

                    <h4 className="mb-3 text-center">
                        Crear cuenta
                    </h4>

                    <form onSubmit={onSubmit}>

                        {/* EMAIL */}

                        <div className="mb-3">

                            <label className="form-label">
                                Correo electrónico
                            </label>

                            <input
                                type="email"
                                className={`form-control form-control-lg ${email && !emailValid ? "is-invalid" : ""}`}
                                value={email}
                                onChange={(e) => {

                                    const newEmail = e.target.value;

                                    setEmail(newEmail);

                                    setErrors((prev) => {

                                        if (!prev.email) return prev;

                                        const updated = { ...prev };

                                        delete updated.email;

                                        return updated;

                                    });

                                }}
                                required
                            />


                            {errors.email && (
                                <div className="text-danger mt-1">
                                    {errors.email}
                                </div>
                            )}

                        </div>


                        {/* PASSWORD */}

                        <div className="mb-4">

                            <label className="form-label">
                                Contraseña
                            </label>

                            <input
                                type="password"
                                className="form-control form-control-lg"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />

                            <div className="form-text mt-2 text-body">

                                <div className={password.length >= 8 ? "text-success" : ""}>
                                    • Mínimo 8 caracteres
                                </div>

                                <div className={/[A-Z]/.test(password) ? "text-success" : ""}>
                                    • Al menos una letra mayúscula
                                </div>

                                <div className={/[a-z]/.test(password) ? "text-success" : ""}>
                                    • Al menos una letra minúscula
                                </div>

                                <div className={/\d/.test(password) ? "text-success" : ""}>
                                    • Al menos un número
                                </div>

                            </div>

                            {errors.password && (
                                <div className="text-danger mt-1">
                                    {errors.password}
                                </div>
                            )}

                        </div>


                        {/* FIRST NAME */}

                        <div className="mb-3">

                            <label className="form-label">
                                Nombre
                            </label>

                            <input
                                type="text"
                                className="form-control form-control-lg"
                                value={firstName}
                                onChange={(e) => setFirstName(e.target.value)}
                                required
                            />

                            {errors.firstName && (
                                <div className="text-danger mt-1">
                                    {errors.firstName}
                                </div>
                            )}

                        </div>


                        {/* LAST NAME FATHER */}

                        <div className="mb-3">

                            <label className="form-label">
                                Apellido paterno
                            </label>

                            <input
                                type="text"
                                className="form-control form-control-lg"
                                value={lastNameFather}
                                onChange={(e) => setLastNameFather(e.target.value)}
                                required
                            />

                            {errors.lastNameFather && (
                                <div className="text-danger mt-1">
                                    {errors.lastNameFather}
                                </div>
                            )}

                        </div>


                        {/* LAST NAME MOTHER */}

                        <div className="mb-3">

                            <label className="form-label">
                                Apellido materno
                            </label>

                            <input
                                type="text"
                                className="form-control form-control-lg"
                                value={lastNameMother}
                                onChange={(e) => setLastNameMother(e.target.value)}
                                required
                            />

                            {errors.lastNameMother && (
                                <div className="text-danger mt-1">
                                    {errors.lastNameMother}
                                </div>
                            )}

                        </div>


                        {/* REFERRAL CODE */}

                        <div className="mb-4">

                            <label className="form-label">
                                Código de referido (opcional)
                            </label>

                            <input
                                type="text"
                                className="form-control form-control-lg"
                                value={referralCode}
                                onChange={(e) => setReferralCode(e.target.value)}
                            />

                        </div>


                        {/* BUTTON */}

                        <div className="d-grid mt-3 position-relative">

                            <button
                                className="btn btn-primary btn-lg"
                                type="submit"
                            >
                                Crear cuenta
                            </button>

                            {showAlert && (
                                <div
                                    style={{
                                        position: "absolute",
                                        right: "-260px",
                                        top: "0",
                                        background: "#ffe5e5",
                                        border: "1px solid #ff4d4f",
                                        color: "#b00020",
                                        padding: "10px 14px",
                                        borderRadius: "8px",
                                        fontSize: "14px",
                                        width: "240px",
                                        boxShadow: "0 2px 10px rgba(0,0,0,0.15)"
                                    }}
                                >
                                    ⚠ Completa los campos obligatorios
                                </div>
                            )}

                        </div>

                    </form>

                </div>

            </div>

        </div>

    );

};