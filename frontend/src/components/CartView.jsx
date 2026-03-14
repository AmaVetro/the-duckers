//frontend/src/components/CartView.jsx:

import { useNavigate } from "react-router-dom";
import { clp } from "../utils/currency";
import { useCart } from "../hooks/useCart";
import { useEffect } from "react";

export const CartView = ({ handlerDelete, handlerUpdateQuantity, items }) => {
  
  const navigate = useNavigate();
  const { loadCart } = useCart();

  useEffect(() => {
    loadCart();
  }, []);


  const onDeleteProduct = (itemId) => {

    handlerDelete(itemId);

  };

  // ===============================
  // Quantity handlers
  // ===============================

  const handleIncrease = (item) => {

    const newQuantity = item.quantity + 1;

    handlerUpdateQuantity(item.itemId, newQuantity);

  };

  const handleDecrease = (item) => {

    const newQuantity = item.quantity - 1;

    if (newQuantity < 1) {

      handlerDelete(item.itemId);

      return;

    }

    handlerUpdateQuantity(item.itemId, newQuantity);

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

          {items.length === 0 ? (

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

                          <button
                            className="btn btn-sm btn-outline-secondary"
                            onClick={() => handleDecrease(item)}
                          >
                            −
                          </button>

                          <span className="fw-semibold">
                            {item.quantity}
                          </span>

                          <button
                            className="btn btn-sm btn-outline-secondary"
                            onClick={() => handleIncrease(item)}
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
                  onClick={() =>
                    navigate("/checkout", {
                      state: {
                        total: items.reduce((sum, item) => sum + item.subtotal, 0),
                        items
                      }
                    })
                  }
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