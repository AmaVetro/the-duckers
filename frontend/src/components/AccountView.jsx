//FrontEndDuckers/src/components/AccountView.jsx:

import React, { useState } from "react";
import { clp } from "../utils/currency";
import { useAuth } from "../hooks/useAuth";   


const profileMock = {
  photoUrl: "",
  username: "arturo_duck",
  fullName: "Arturo Núñez",
  email: "arturo@example.com",
  phone: "+56 9 1234 5678",
  birthdate: "2001-04-20",
  address: "Av. Siempre Viva 742, Santiago",
  points: 12450,
};

const initialsFrom = (nameOrUser) => {
  const parts = (nameOrUser || "").trim().split(" ");
  const first = parts[0]?.[0] ?? "";
  const last = parts[1]?.[0] ?? "";
  return (first + last).toUpperCase();
};


const mockOrders = [
  {
    id: "TDK-2025-00123",
    date: "2025-10-04T15:35:00",
    status: "Entregado",
    itemsCount: 3,
    total: 18990 + 46990 + 74990,
    payment: "Tarjeta •••• 1234",
    shippingSummary: "Entrega a domicilio, Ñuñoa",
    items: [
      { name: "Auriculares Force 2", qty: 1, price: 18990 },
      { name: "Mouse RobotRat", qty: 1, price: 46990 },
      { name: "Teclado Platforms 5", qty: 1, price: 74990 },
    ],
  },
  {
    id: "TDK-2025-00145",
    date: "2025-10-21T11:10:00",
    status: "En camino",
    itemsCount: 2,
    total: 45990 + 84990,
    payment: "Tarjeta •••• 5678",
    shippingSummary: "Retiro en tienda, Santiago Centro",
    items: [
      { name: "Teclado Amazonia R50", qty: 1, price: 45990 },
      { name: "Silla Gamer Comfy Throne 3", qty: 1, price: 84990 },
    ],
  },
  {
    id: "TDK-2025-00187",
    date: "2025-11-02T18:48:00",
    status: "Procesando",
    itemsCount: 1,
    total: 287990,
    payment: "Transferencia bancaria",
    shippingSummary: "Entrega a domicilio, Providencia",
    items: [{ name: "VR Headset Luminous Vision", qty: 1, price: 287990 }],
  },
];

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
  const [expandedId, setExpandedId] = useState(null);
  const { user } = useAuth();  

  const toggle = (id) => setExpandedId((cur) => (cur === id ? null : id));

  const currentUsername = user?.username || profileMock.username;
  const currentName = user?.name || profileMock.fullName;
  const currentEmail = user?.email || profileMock.email;

  const initials = initialsFrom(currentName || currentUsername);

  return (
    <div className="container my-4">
      <h3 className="mb-3">Mi Cuenta</h3>

      <div className="row g-3">
        <div className="col-12 col-lg-6">
          <div className="card account-card">
            <div className="card-body">

              <div className="d-flex flex-column align-items-center text-center mb-3">
                <div className="avatar-circle mb-2">
                  {profileMock.photoUrl ? (
                    <img src={profileMock.photoUrl} alt="Foto de perfil" className="avatar-img" />
                  ) : (
                    <div className="avatar-fallback">{initials}</div>
                  )}
                </div>
              </div>

              <div className="list-group">
                <div className="list-group-item">
                  <small className="text-muted d-block">Usuario</small>
                  <span className="fw-semibold">{currentUsername}</span>
                </div>
                <div className="list-group-item">
                  <small className="text-muted d-block">Nombre</small>
                  <span className="fw-semibold">{currentName}</span>
                </div>
                <div className="list-group-item">
                  <small className="text-muted d-block">Correo</small>
                  <span className="fw-semibold">{currentEmail}</span>
                </div>
                <div className="list-group-item">
                  <small className="text-muted d-block">Celular</small>
                  <span className="fw-semibold">{profileMock.phone}</span>
                </div>
                <div className="list-group-item">
                  <small className="text-muted d-block">Fecha de nacimiento</small>
                  <span className="fw-semibold">
                    {new Date(profileMock.birthdate).toLocaleDateString("es-CL")}
                  </span>
                </div>
                <div className="list-group-item">
                  <small className="text-muted d-block">Dirección de despacho</small>
                  <span className="fw-semibold">{profileMock.address}</span>
                </div>
                <div className="list-group-item">
                  <small className="text-muted d-block">Puntos acumulados</small>
                  <span className="fw-semibold">
                    {profileMock.points.toLocaleString("es-CL")}
                  </span>
                </div>
              </div>

              <div className="text-end mt-3">
                <button className="btn btn-outline-secondary btn-sm" onClick={() => alert("Demo: sin edición")}>
                  Editar
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* HISTORIAL (mock) */}
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
                    {mockOrders.map((o) => {
                      const fecha = new Date(o.date).toLocaleDateString("es-CL");
                      const expanded = expandedId === o.id;

                      return (
                        <React.Fragment key={o.id}>
                          <tr>
                            <td className="fw-medium">{o.id}</td>
                            <td>{fecha}</td>
                            <td>
                              <span className={`badge bg-${badgeFor(o.status)}`}>{o.status}</span>
                            </td>
                            <td className="text-center">{o.itemsCount}</td>
                            <td className="text-end">{clp(o.total)}</td>
                            <td className="text-end">
                              <button className="btn btn-sm btn-outline-secondary" onClick={() => toggle(o.id)}>
                                {expanded ? "Ocultar" : "Ver detalle"}
                              </button>
                            </td>
                          </tr>

                          {expanded && (
                            <tr>
                              <td colSpan={6} className="bg-purple">
                                <div className="p-3">
                                  <div className="mb-2">
                                    <strong>Método de pago:</strong> {o.payment}
                                  </div>
                                  <div className="mb-3">
                                    <strong>Envío:</strong> {o.shippingSummary}
                                  </div>

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
                                        {o.items.map((it, idx) => (
                                          <tr key={idx}>
                                            <td>{it.name}</td>
                                            <td className="text-center">{it.qty}</td>
                                            <td className="text-end">{clp(it.price)}</td>
                                            <td className="text-end">{clp(it.price * it.qty)}</td>
                                          </tr>
                                        ))}
                                      </tbody>
                                      <tfoot>
                                        <tr>
                                          <td colSpan={3} className="text-end fw-bold">Total</td>
                                          <td className="text-end fw-bold">{clp(o.total)}</td>
                                        </tr>
                                      </tfoot>
                                    </table>
                                  </div>
                                </div>
                              </td>
                            </tr>
                          )}
                        </React.Fragment>
                      );
                    })}
                  </tbody>
                </table>
              </div>

              <div className="d-flex justify-content-between align-items-center mt-3">
                <button className="btn btn-sm btn-outline-primary" onClick={() => alert("Demo: PDF")}>
                  Descargar comprobante
                </button>
              </div>
            </div>
          </div>
        </div>
        {/* /HISTORIAL */}
      </div>
    </div>
  );
};
