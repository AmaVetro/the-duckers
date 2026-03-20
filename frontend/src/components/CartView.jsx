//frontend/src/components/CartView.jsx:

import { useNavigate } from "react-router-dom";
import { clp } from "../utils/currency";
import { useState } from "react";

export const CartView = ({ handlerDelete, handlerUpdateQuantity, items, loadingCart }) => {
  
  const navigate = useNavigate();
  const [loadingItems, setLoadingItems] = useState({});

  const onDeleteProduct = async (itemId) => {

    setLoadingItems(prev => ({ ...prev, [itemId]: true }));

    try {
      await handlerDelete(itemId);
    } finally {
      setLoadingItems(prev => ({ ...prev, [itemId]: false }));
    }

  };

  // ===============================
  // Quantity handlers
  // ===============================

  const handleIncrease = async (item) => {

    setLoadingItems(prev => ({ ...prev, [item.itemId]: true }));

    try {
      const newQuantity = item.quantity + 1;
      await handlerUpdateQuantity(item.itemId, newQuantity);
    } finally {
      setLoadingItems(prev => ({ ...prev, [item.itemId]: false }));
    }

  };


  const handleDecrease = async (item) => {

    setLoadingItems(prev => ({ ...prev, [item.itemId]: true }));

    try {

      const newQuantity = item.quantity - 1;

      if (newQuantity < 1) {
        await handlerDelete(item.itemId);
        return;
      }

      await handlerUpdateQuantity(item.itemId, newQuantity);

    } finally {
      setLoadingItems(prev => ({ ...prev, [item.itemId]: false }));
    }

  };




  const onCatalog = () => {

    navigate("/catalog");

  };

  const cartTotal = items.reduce((acc, item) => acc + item.subtotal, 0);

  return (

    <div className="d-flex flex-column" style={{ minHeight: "100%" }}>

      <div className="flex-grow-1 pb-5">

        <div className="container">

          <h3>Carro de compras</h3>

          {loadingCart ? (

            <div className="alert alert-info">
              Cargando carrito...
            </div>

          ) : items.length === 0 ? (

            <div className="alert alert-warning">
              Tu carrito está vacío.
            </div>

          ) : (

            <>
              <table className="table table-hover table-striped">

                <thead>

                  <tr>
                    <th>Producto</th>
                    <th>Precio</th>
                    <th>Cantidad</th>
                    <th>Subtotal</th>
                    <th>Eliminar</th>
                  </tr>

                </thead>

                <tbody>

                  {items.map((item) => (

                    <tr key={item.itemId}>

                      <td>{item.productName}</td>

                      <td>{clp(item.unitPrice)}</td>

                      <td>
                        <div className="d-flex align-items-center justify-content-center gap-2">

                          {/* LOADER */}
                          {loadingItems[item.itemId] && (
                            <div
                              className="spinner-border spinner-border-sm text-secondary"
                              role="status"
                              style={{ width: "1rem", height: "1rem" }}
                            >
                              <span className="visually-hidden">Loading...</span>
                            </div>
                          )}

                          <button
                            className="btn btn-sm btn-outline-secondary"
                            onClick={() => handleDecrease(item)}
                            disabled={loadingItems[item.itemId]}
                          >
                            −
                          </button>

                          <span className="fw-semibold">
                            {item.quantity}
                          </span>

                          <button
                            className="btn btn-sm btn-outline-secondary"
                            onClick={() => handleIncrease(item)}
                            disabled={loadingItems[item.itemId]}
                          >
                            +
                          </button>

                        </div>
                      </td>

                      <td>{clp(item.subtotal)}</td>

                      <td>

                        <button
                          className="btn btn-danger"
                          onClick={() => onDeleteProduct(item.itemId)}
                          disabled={loadingItems[item.itemId]}
                        >
                          eliminar
                        </button>

                      </td>

                    </tr>

                  ))}

                </tbody>

              </table>

              <div className="mb-4">

                <button
                  className="btn btn-success"
                  onClick={onCatalog}
                >
                  Seguir comprando
                </button>

                <button
                  className="btn btn-primary ms-2"
                  onClick={() => navigate("/checkout")}
                >
                  Realizar pago
                </button>

              </div>

            </>

          )}

        </div>

      </div>

    </div>

  );

};