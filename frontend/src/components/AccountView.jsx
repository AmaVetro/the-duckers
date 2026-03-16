//frontend/src/components/AccountView.jsx:

import React, { useState, useEffect } from "react";
import { clp } from "../utils/currency";
import { getProfile } from "../services/profileService";
import { getOrders, cancelOrder } from "../services/orderService";
import { useNavigate } from "react-router-dom";


const initialsFrom = (value) => {
  const parts = (value || "").trim().split(" ");
  const first = parts[0]?.[0] ?? "";
  const last = parts[1]?.[0] ?? "";
  return (first + last).toUpperCase();
};


const badgeFor = (status) => {
  switch (status) {
    case "Entregado":
      return "success";
    case "En camino":
      return "primary";
    case "Procesando":
      return "warning";
    case "Cancelado":
      return "danger";
    default:
      return "secondary";
  }
};

export const AccountView = () => {

  const navigate = useNavigate();
  const [expandedId, setExpandedId] = useState(null);

  const [profile, setProfile] = useState(null);
  const [loadingProfile, setLoadingProfile] = useState(true);

  const [orders, setOrders] = useState([]);
  const [copied, setCopied] = useState(false);


  const loadOrders = async () => {
    try {
      const data = await getOrders();
      setOrders(data);
    } catch (error) {
      console.error("Error loading orders", error);
    }
  };


  const handleCancelOrder = async (orderId) => {

    try {

      await cancelOrder(orderId);

      await loadOrders();

    } catch (error) {

      console.error("Error cancelling order", error);

      alert("No se pudo cancelar la orden");

    }

  };


  useEffect(() => {

    const loadProfile = async () => {

      try {

        const data = await getProfile();
        setProfile(data);

      } catch (error) {

        console.error("Error loading profile", error);

      } finally {

        setLoadingProfile(false);

      }

    };

    loadProfile();
    loadOrders();

  }, []);


  const toggle = (id) => setExpandedId((cur) => (cur === id ? null : id));



  const copyReferralCode = async () => {
    try {
      await navigator.clipboard.writeText(referralCode);

      setCopied(true);

      setTimeout(() => {
        setCopied(false);
      }, 2000);

    } catch (err) {
      console.error("Error copying referral code", err);
    }
  };


  const email = profile?.email || "Loading...";
  const level = profile?.level || "-";
  const points = profile?.points || 0;
  const referralCode = profile?.referralCode || "-";
  

  const initials = initialsFrom(email);



  return (
    <div className="container my-4">

      <h3 className="mb-3">Mi Cuenta</h3>

      <div className="row g-3">

        {/* PERFIL */}
        <div className="col-12 col-lg-6">

          <div className="card account-card">
            <div className="card-body">

              <div className="d-flex flex-column align-items-center text-center mb-3">
                <div className="avatar-circle mb-2">
                  <div className="avatar-fallback">{initials}</div>
                </div>
              </div>

              <div className="list-group">
                <div className="list-group-item">
                  <small className="text-muted d-block">Correo</small>
                  <span className="fw-semibold">{email}</span>
                </div>

                <div className="list-group-item">
                  <small className="text-muted d-block">Nivel</small>
                  <span className="fw-semibold">{level}</span>
                </div>

                <div className="list-group-item">
                  <small className="text-muted d-block">Puntos acumulados</small>
                  <span className="fw-semibold">
                    {points.toLocaleString("es-CL")}
                  </span>
                </div>
              </div>

            </div>
          </div>


          {/* CODIGO REFERIDO */}
          <div className="card account-card mt-3">
            <div className="card-body text-center">

              <h5 className="card-title mb-3">Tu código referido:</h5>

              <div className="d-flex justify-content-center align-items-center gap-2 mb-2">

                <div
                  className="py-2 px-3"
                  style={{
                    border: "1px solid #7c4dff",
                    borderRadius: "6px",
                    fontWeight: "bold",
                    letterSpacing: "1px"
                  }}
                >
                  {referralCode}
                </div>

                <button
                  className="btn btn-sm btn-outline-primary"
                  onClick={copyReferralCode}
                  title="Copiar código"
                >
                  📋
                </button>

              </div>

              {copied && (
                <div className="text-success small mb-2">
                  ✔ Código copiado
                </div>
              )}

              <p className="small mb-0" style={{ color: "#ffffff" }}>
                ¡Haz que tus amigos se registren usando este código y ambos ganarán puntos!
              </p>

            </div>
          </div>

        </div>


        {/* HISTORIAL */}
        <div className="col-12 col-lg-6">

          <div className="card h-100">

            <div className="card-body">

              <h5 className="card-title mb-3">Historial de compras</h5>

              <div className="table-responsive">

                <table className="table align-middle">

                  <thead>
                    <tr>
                      <th>N° Pedido</th>
                      <th>Fecha</th>
                      <th>Estado</th>
                      <th className="text-center">Ítems</th>
                      <th className="text-end">Total</th>
                      <th></th>
                    </tr>
                  </thead>



                  <tbody>

                    {orders.length === 0 ? (

                      <tr>
                        <td colSpan={6} className="text-center py-5">

                          <div className="d-flex flex-column align-items-center">

                            <h5 className="mb-2">
                              ¡Ups! No tienes órdenes.
                            </h5>

                            <p className="text-white mb-3">
                              Explora nuestro catálogo y encuentra tu próximo producto.
                            </p>

                            <button
                              className="btn btn-primary"
                              onClick={() => navigate("/catalog")}
                            >
                              Ver catálogo
                            </button>

                          </div>

                        </td>
                      </tr>

                    ) : (

                      orders.map((o) => {

                        const fecha = new Date(o.createdAt).toLocaleDateString("es-CL");
                        const expanded = expandedId === o.orderId;

                        return (
                          <React.Fragment key={o.orderId}>

                            <tr>

                              <td className="fw-medium">{o.orderId}</td>

                              <td>{fecha}</td>

                              <td>
                                <span className={`badge bg-${badgeFor(o.status)}`}>
                                  {o.status}
                                </span>
                              </td>

                              <td className="text-center">{o.items?.length ?? 0}</td>

                              <td className="text-end">{clp(o.total)}</td>



                              <td className="text-end">

                                <div className="d-flex gap-2 justify-content-end">

                                  <button
                                    className="btn btn-sm btn-outline-secondary"
                                    onClick={() => toggle(o.orderId)}
                                  >
                                    {expanded ? "Ocultar" : "Ver detalle"}
                                  </button>

                                  {o.status === "PENDING" && (

                                    <button
                                      className="btn btn-sm btn-outline-danger"
                                      onClick={() => handleCancelOrder(o.orderId)}
                                    >
                                      Cancelar
                                    </button>
                                  )}

                                  {o.status === "PENDING" && (

                                    <button
                                      className="btn btn-sm btn-outline-primary"
                                      onClick={() => navigate(`/payment/${o.orderId}`)}
                                    >
                                      Continuar
                                    </button>

                                  )}

                                  

                                </div>

                              </td>



                            </tr>


                            {expanded && (

                              <tr>

                                <td colSpan={6} className="bg-purple">

                                  <div className="p-3">
                                    
                                    <div className="table-responsive">

                                      <table className="table table-sm">

                                        <thead>
                                          <tr>
                                            <th>Producto</th>
                                            <th className="text-center">Cant.</th>
                                            <th className="text-end">Precio</th>
                                            <th className="text-end">Subtotal</th>
                                          </tr>
                                        </thead>

                                        <tbody>

                                          {o.items?.map((it, idx) => (

                                            <tr key={idx}>

                                              <td>{it.productName}</td>

                                              <td className="text-center">{it.quantity}</td>

                                              <td className="text-end">{clp(it.unitPrice)}</td>

                                              <td className="text-end">
                                                {clp(it.subtotal)}
                                              </td>

                                            </tr>

                                          ))}

                                        </tbody>


                                      </table>

                                      <div className="mt-3">

                                        <h6 className="fw-bold mb-2">
                                          Resumen del pago
                                        </h6>

                                        <div className="d-flex justify-content-between">
                                          <span>Subtotal</span>
                                          <span>{clp(o.subtotal)}</span>
                                        </div>

                                        <div className="d-flex justify-content-between">
                                          <span>Descuento DUOC UC</span>
                                          <span className="text-success">- {clp(o.duocDiscount)}</span>
                                        </div>

                                        <div className="d-flex justify-content-between">
                                          <span>Descuento por puntos</span>
                                          <span className="text-success">- {clp(o.pointsDiscount)}</span>
                                        </div>

                                        <div className="d-flex justify-content-between">
                                          <span>IVA (19%)</span>
                                          <span>{clp(o.iva)}</span>
                                        </div>

                                        <hr />

                                        <div className="d-flex justify-content-between fw-bold">
                                          <span>Total final</span>
                                          <span>{clp(o.total)}</span>
                                        </div>

                                      </div>



                                    </div>

                                  </div>

                                </td>

                              </tr>

                            )}

                          </React.Fragment>
                        );
                      })
                    )}

                  </tbody>




                </table>

              </div>

            </div>

          </div>

        </div>

        {/* /HISTORIAL */}

      </div>

    </div>
  );

};