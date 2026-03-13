//frontend/src/components/PaymentView.jsx:


import { useParams, useNavigate } from "react-router-dom";
import { useState, useEffect} from "react";
import { payOrder, getOrderById } from "../services/orderService";
import { clp } from "../utils/currency";


export const PaymentView = () => {

    const { orderId } = useParams();
    const navigate = useNavigate();

    const [paymentMethod, setPaymentMethod] = useState("tarjeta");
    const [deliveryMethod, setDeliveryMethod] = useState("despacho");
    const [processing, setProcessing] = useState(false);
    const [orderTotal, setOrderTotal] = useState(0);


    const handleConfirmPayment = async () => {

        try {

        setProcessing(true);

        await payOrder(orderId);

        navigate(`/order-confirmation/${orderId}`);

        } catch (error) {

        console.error("Payment error:", error);

        alert("Error procesando el pago");

        } finally {

        setProcessing(false);

        }

    };


    useEffect(() => {

        const loadOrder = async () => {

            try {

                const order = await getOrderById(orderId);

                setOrderTotal(order.total);

            } catch (error) {

                console.error("Error loading order", error);

            }

        };

        loadOrder();

    }, [orderId]);


    return (

        <div className="container my-4">

        <h3 className="mb-3">Pago</h3>

        <div className="row g-3">

            <div className="col-12 col-lg-8">

            <div className="card">

                <div className="card-body">

                <h5 className="card-title mb-3">
                    Selecciona método de pago
                </h5>


                {/* PAYMENT METHOD */}

                <div className="mb-3">

                    <label className="form-label fw-semibold">
                    Método de pago
                    </label>

                    <div className="d-flex gap-3 flex-wrap">

                    <div className="form-check">

                        <input
                        className="form-check-input"
                        type="radio"
                        value="tarjeta"
                        checked={paymentMethod === "tarjeta"}
                        onChange={(e) => setPaymentMethod(e.target.value)}
                        />

                        <label className="form-check-label">
                        Tarjeta
                        </label>

                    </div>


                    <div className="form-check">

                        <input
                        className="form-check-input"
                        type="radio"
                        value="transferencia"
                        checked={paymentMethod === "transferencia"}
                        onChange={(e) => setPaymentMethod(e.target.value)}
                        />

                        <label className="form-check-label">
                        Transferencia
                        </label>

                    </div>


                    <div className="form-check">

                        <input
                        className="form-check-input"
                        type="radio"
                        value="webpay"
                        checked={paymentMethod === "webpay"}
                        onChange={(e) => setPaymentMethod(e.target.value)}
                        />

                        <label className="form-check-label">
                        Webpay
                        </label>

                    </div>

                    </div>

                </div>


                {/* DELIVERY METHOD */}

                <div className="mb-3">

                    <label className="form-label fw-semibold">
                    Método de entrega
                    </label>

                    <div className="d-flex gap-3 flex-wrap">

                    <div className="form-check">

                        <input
                        className="form-check-input"
                        type="radio"
                        value="despacho"
                        checked={deliveryMethod === "despacho"}
                        onChange={(e) => setDeliveryMethod(e.target.value)}
                        />

                        <label className="form-check-label">
                        Despacho a domicilio
                        </label>

                    </div>


                    <div className="form-check">

                        <input
                        className="form-check-input"
                        type="radio"
                        value="retiro"
                        checked={deliveryMethod === "retiro"}
                        onChange={(e) => setDeliveryMethod(e.target.value)}
                        />

                        <label className="form-check-label">
                        Retiro en tienda
                        </label>

                    </div>

                    </div>

                </div>


                <hr />

                <div className="d-flex justify-content-between fw-bold mb-3">
                    <span>Total final</span>
                    <span>{clp(orderTotal)}</span>
                </div>


                <div className="d-flex gap-2">

                    <button
                    className="btn btn-secondary"
                    onClick={() => navigate("/account")}
                    >
                    Volver
                    </button>


                    <button
                    className="btn btn-success"
                    onClick={handleConfirmPayment}
                    disabled={processing}
                    >
                    {processing ? "Procesando..." : "Confirmar pago"}
                    </button>

                </div>

                </div>

            </div>

            </div>

        </div>

        </div>

    );

};