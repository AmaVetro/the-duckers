//frontend/src/components/CheckoutView.jsx:

import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { clp } from "../utils/currency";
import { checkout } from "../services/orderService";
import { getCart } from "../services/cartService";
import { getProfile } from "../services/profileService";

export const CheckoutView = () => {

  const navigate = useNavigate();

  const [cartTotal, setCartTotal] = useState(0);
  const [userPoints, setUserPoints] = useState(0);
  const [userEmail, setUserEmail] = useState("");
  const [usePoints, setUsePoints] = useState(false);
  const [pointsDiscount, setPointsDiscount] = useState(0);
  const [duocDiscount, setDuocDiscount] = useState(0);
  const [ivaAmount, setIvaAmount] = useState(0);
  const [finalTotal, setFinalTotal] = useState(0);
  const [processing, setProcessing] = useState(false);
  

  useEffect(() => {

    const loadCartTotal = async () => {

      try {

        const cart = await getCart();

        const total = cart.items?.reduce(
          (sum, item) => sum + item.subtotal,
          0
        ) ?? 0;

        setCartTotal(total);

      } catch (error) {

        console.error("Error loading cart total", error);

      }

    };


    const loadUserPoints = async () => {

      try {

        const profile = await getProfile();

        setUserPoints(profile.points ?? 0);
        setUserEmail(profile.email ?? "");

      } catch (error) {

        console.error("Error loading user points", error);

      }

    };

    loadCartTotal();
    loadUserPoints();

  }, []);




  useEffect(() => {

    if (!usePoints) {

      setPointsDiscount(0);
      return;

    }

    const maxDiscount = cartTotal * 0.30;

    const availableDiscount = userPoints / 100;

    const discount = Math.min(availableDiscount, maxDiscount);

    setPointsDiscount(Math.floor(discount));

  }, [usePoints, userPoints, cartTotal]);



  useEffect(() => {

    if (!userEmail) {

      setDuocDiscount(0);
      return;

    }

    const isDuocUser = userEmail.endsWith("@duocuc.cl");

    if (!isDuocUser) {

      setDuocDiscount(0);
      return;

    }

    const discount = cartTotal * 0.10;

    setDuocDiscount(Math.floor(discount));

  }, [userEmail, cartTotal]);


  useEffect(() => {

    const taxableBase = cartTotal - pointsDiscount - duocDiscount;

    if (taxableBase <= 0) {

      setIvaAmount(0);
      setFinalTotal(0);
      return;

    }

    const iva = Math.floor(taxableBase * 0.19);

    setIvaAmount(iva);

    const total = taxableBase + iva;

    setFinalTotal(Math.floor(total));

  }, [cartTotal, pointsDiscount, duocDiscount]);




  const handleConfirm = async (e) => {

    e.preventDefault();

    try {

      setProcessing(true);

      const order = await checkout(usePoints);

      const orderId = order.orderId;

      // refresh cart after checkout
      window.dispatchEvent(new Event("cart-updated"));

      navigate(`/payment/${orderId}`);

    } catch (error) {

      console.error("Checkout error:", error);

      alert("Error creando la orden");

    } finally {

      setProcessing(false);

    }

  };

  return (
    <div className="container my-4">
      <h3 className="mb-3">Checkout</h3>

      <div className="row g-3">

        <div className="col-12 col-lg-8">

          <div className="card">

            <div className="card-body">

              <h5 className="card-title mb-3">
                Resumen de pago
              </h5>

              <div className="mb-3">
                <label className="form-label fw-semibold">
                  Total estimado del carrito
                </label>

                <div className="form-control" readOnly>
                  {clp(cartTotal)}
                </div>
              </div>

              <div className="mb-3">

                <label className="form-label fw-semibold">
                  ¿Usar puntos?
                </label>

                <div className="d-flex gap-3">

                  <div className="form-check">

                    <input
                      className="form-check-input"
                      type="radio"
                      name="usePoints"
                      value="yes"
                      checked={usePoints === true}
                      disabled={userPoints === 0}
                      onChange={() => setUsePoints(true)}
                    />

                    <label className="form-check-label">
                      Sí
                    </label>

                  </div>

                  <div className="form-check">

                    <input
                      className="form-check-input"
                      type="radio"
                      name="usePoints"
                      value="no"
                      checked={!usePoints}
                      onChange={() => setUsePoints(false)}
                    />

                    <label className="form-check-label">
                      No
                    </label>

                  </div>

                </div>

                <small className="text-white">
                  Puntos disponibles: {userPoints.toLocaleString("es-CL")}
                </small>

              </div>

              <div className="d-flex gap-2">

                <button
                  className="btn btn-secondary"
                  onClick={() => navigate("/cart")}
                >
                  Volver al carrito
                </button>

                <button
                  type="button"
                  className="btn btn-success"
                  onClick={handleConfirm}
                  disabled={processing}
                >
                  {processing ? "Procesando..." : "Confirmar pago"}
                </button>

              </div>

            </div>

          </div>

        </div>

        <div className="col-12 col-lg-4">

          <div className="card">

            <div className="card-body">

              <h6 className="fw-bold mb-2">
                Detalle
              </h6>

              <div className="d-flex justify-content-between">
                <span>Total estimado</span>
                <span className="fw-semibold">
                  {clp(cartTotal)}
                </span>
              </div>

              <div className="d-flex justify-content-between">
                <span>Descuento por puntos</span>
                <span className="text-success">
                  - {clp(pointsDiscount)}
                </span>
              </div>

              <div className="d-flex justify-content-between">
                <span>Descuento DUOC UC</span>
                <span className="text-success">
                  - {clp(duocDiscount)}
                </span>
              </div>

              <div className="d-flex justify-content-between">
                <span>IVA (19%)</span>
                <span>
                  {clp(ivaAmount)}
                </span>
              </div>

              <hr />

              <div className="d-flex justify-content-between fw-bold">
                <span>Total final</span>
                <span>{clp(finalTotal)}</span>
              </div>

            </div>

          </div>

        </div>

      </div>

    </div>
  );

};