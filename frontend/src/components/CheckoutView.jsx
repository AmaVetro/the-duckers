//FrontEndDuckers/src/components/CheckoutView.jsx:

import { useLocation, useNavigate } from "react-router-dom";
import { useMemo, useState } from "react";
import { clp } from "../utils/currency";
import { checkout, payOrder } from "../services/orderService";
import { getCart } from "../services/cartService";




export const CheckoutView = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const previewTotal = location?.state?.total ?? 0;
  
  const [paymentMethod, setPaymentMethod] = useState("tarjeta");
  const [deliveryMethod, setDeliveryMethod] = useState("despacho");
  const [orderId, setOrderId] = useState(null);

  const orderTotal = useMemo(() => previewTotal, [previewTotal]);

  const createOrder = async () => {

    const order = await checkout(false);

    setOrderId(order.id);

    return order.id;

  };

  const executePayment = async (orderId) => {
    await payOrder(orderId);
  };

  const handleConfirm = async (e) => {
    e.preventDefault();
    try {
      const id = await createOrder();
      await executePayment(id);
      alert("Pago completado exitosamente");
      navigate("/account");
    } catch (error) {
      console.error("Checkout error:", error);
      alert("Error procesando la orden");
    }
  };

  return (
    <div className="container my-4">
      <h3 className="mb-3">Checkout</h3>

      <div className="row g-3">
        <div className="col-12 col-lg-8">
          <div className="card">
            <div className="card-body">
              <h5 className="card-title mb-3">Resumen de pago</h5>

              <div className="mb-3">
                <label className="form-label fw-semibold">Total estimado del carrito</label>
                <div className="form-control" readOnly>
                  {clp(orderTotal)}
                </div>
              </div>

              <div className="mb-3">
                <label className="form-label fw-semibold">Método de pago</label>
                <div className="d-flex gap-3 flex-wrap">
                  <div className="form-check">
                    <input
                      className="form-check-input"
                      type="radio"
                      name="paymentMethod"
                      id="pago_tarjeta"
                      value="tarjeta"
                      checked={paymentMethod === "tarjeta"}
                      onChange={(e) => setPaymentMethod(e.target.value)}
                    />
                    <label className="form-check-label" htmlFor="pago_tarjeta">
                      Tarjeta
                    </label>
                  </div>

                  <div className="form-check">
                    <input
                      className="form-check-input"
                      type="radio"
                      name="paymentMethod"
                      id="pago_transferencia"
                      value="transferencia"
                      checked={paymentMethod === "transferencia"}
                      onChange={(e) => setPaymentMethod(e.target.value)}
                    />
                    <label className="form-check-label" htmlFor="pago_transferencia">
                      Transferencia
                    </label>
                  </div>

                  <div className="form-check">
                    <input
                      className="form-check-input"
                      type="radio"
                      name="paymentMethod"
                      id="pago_webpay"
                      value="webpay"
                      checked={paymentMethod === "webpay"}
                      onChange={(e) => setPaymentMethod(e.target.value)}
                    />
                    <label className="form-check-label" htmlFor="pago_webpay">
                      Webpay
                    </label>
                  </div>
                </div>
              </div>

              <div className="mb-3">
                <label className="form-label fw-semibold">Entrega</label>
                <div className="d-flex gap-3 flex-wrap">
                  <div className="form-check">
                    <input
                      className="form-check-input"
                      type="radio"
                      name="deliveryMethod"
                      id="entrega_despacho"
                      value="despacho"
                      checked={deliveryMethod === "despacho"}
                      onChange={(e) => setDeliveryMethod(e.target.value)}
                    />
                    <label className="form-check-label" htmlFor="entrega_despacho">
                      Despacho a domicilio
                    </label>
                  </div>

                  <div className="form-check">
                    <input
                      className="form-check-input"
                      type="radio"
                      name="deliveryMethod"
                      id="entrega_retiro"
                      value="retiro"
                      checked={deliveryMethod === "retiro"}
                      onChange={(e) => setDeliveryMethod(e.target.value)}
                    />
                    <label className="form-check-label" htmlFor="entrega_retiro">
                      Retiro en tienda
                    </label>
                  </div>
                </div>
              </div>

              <div className="d-flex gap-2">
                <button className="btn btn-secondary" onClick={() => navigate("/cart")}>
                  Volver al carrito
                </button>
                <button className="btn btn-success" onClick={handleConfirm}>
                  Confirmar pago
                </button>
              </div>
            </div>
          </div>
        </div>

        <div className="col-12 col-lg-4">
          <div className="card">
            <div className="card-body">
              <h6 className="fw-bold mb-2">Detalle</h6>
              <div className="d-flex justify-content-between">
                <span>Total estimado</span>
                <span className="fw-semibold">{clp(orderTotal)}</span>
              </div>
              <hr />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
