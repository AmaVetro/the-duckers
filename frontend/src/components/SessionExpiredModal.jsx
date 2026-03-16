//frontend/src/components/SessionExpiredModal.jsx:

import { useNavigate } from "react-router-dom";

export const SessionExpiredModal = ({ visible, onClose }) => {

    const navigate = useNavigate();

    if (!visible) return null;

    return (

        <div
            style={{
                position: "fixed",
                top: 0,
                left: 0,
                width: "100vw",
                height: "100vh",
                backgroundColor: "rgba(0,0,0,0.55)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                zIndex: 9999
            }}
        >

            <div
                style={{
                    background: "#1e1e1e",
                    padding: "30px",
                    borderRadius: "10px",
                    textAlign: "center",
                    width: "320px",
                    boxShadow: "0 10px 30px rgba(0,0,0,0.4)"
                }}
            >

                <h5 className="mb-3">
                    Su sesión ha expirado.
                </h5>

                <button
                    className="btn btn-primary"
                    onClick={() => {
                        onClose();
                        navigate("/login");
                    }}
                >
                    Iniciar sesión
                </button>

            </div>

        </div>

    );

};