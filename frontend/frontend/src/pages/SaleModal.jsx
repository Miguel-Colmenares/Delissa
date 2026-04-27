import { motion, AnimatePresence } from "framer-motion";
import { useState, useEffect } from "react";
import { createPortal } from "react-dom";
import logo from "../assets/delissa_Logo.png";


export default function SaleModal({ open, onClose, onConfirm, total, cart}) {

  const [step, setStep] = useState(1);
  const [type, setType] = useState(null);
  const [receiptData, setReceiptData] = useState(null);


  const [cashData, setCashData] = useState({
    received: ""
  });

  const formatCOP = (value) => {
  if (!value) return "0";
  return new Intl.NumberFormat("es-CO").format(value);
};

  const [form, setForm] = useState({
    paymentMethod: "CASH",
    clientName: "",
    nit: "",
    email: "",
    address: ""
  });

  const change = cashData.received
    ? Number(cashData.received) - Number(total)
    : 0;

  useEffect(() => {
    document.body.style.overflow = open ? "hidden" : "auto";
  }, [open]);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  const handleSelectType = (selected) => {
    setType(selected);
    setStep(2);
  };

  const handleBack = () => {
    setStep(1);
    setType(null);
  };

  const handleSubmit = () => {

  if (form.paymentMethod === "CASH") {
    if (!cashData.received || Number(cashData.received) < total) {
      alert("❌ El dinero no es suficiente");
      return;
    }
  }

  onConfirm({
    invoiceType: type === "empresa" ? "EMPRESARIAL" : "NORMAL",
    paymentMethod: form.paymentMethod,
    clientInvoice:
      type === "empresa"
        ? {
            clientName: form.clientName,
            nit: form.nit,
            email: form.email,
            address: form.address
          }
        : null
  });

  // 🔥 GUARDAR DATOS DEL RECIBO
  setReceiptData({
  total,
  received: Number(cashData.received || 0),
  change: change >= 0 ? change : 0,
  paymentMethod: form.paymentMethod,
  items: cart
});
  // 🔥 IR AL STEP 3
  setStep(3);
};

  return createPortal(
    <AnimatePresence>
      {open && (
        <>
          {/* 🔥 FONDO */}
          <motion.div
            className="fixed inset-0 bg-black/40 backdrop-blur-sm z-[99999]"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          />

          {/* 🔥 MODAL */}
          <motion.div
            className="fixed inset-0 z-[99999] flex items-center justify-center"
            initial={{ opacity: 0, scale: 0.8, y: 40 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.8, y: 40 }}
          >
            
           <div className="bg-white text-black rounded-3xl shadow-2xl p-6 w-[400px] border border-gray-200 [color-scheme:light]">
              {/* 🔙 */}
              {step === 2 && (
                <button
                  onClick={handleBack}
                  className="text-sm text-gray-500 hover:text-black mb-3"
                >
                  ← Volver
                </button>
              )}

              {/* 🧾 STEP 1 */}
              {step === 1 && (
                <>
                  <h2 className="text-xl font-bold mb-6 text-center text-gray-800">
                    Tipo de factura
                  </h2>

                  <div className="flex flex-col gap-3">
                    <button
                      onClick={() => handleSelectType("normal")}
                      className="bg-gradient-to-r from-[#FF9F1C] to-[#FF4040] text-white py-3 rounded-xl shadow hover:scale-[1.02]"
                    >
                      🧾 Factura Normal
                    </button>

                    <button
                      onClick={() => handleSelectType("empresa")}
                      className="bg-gray-100 py-3 rounded-xl shadow hover:bg-gray-200"
                    >
                      🏢 Factura Empresarial
                    </button>
                  </div>
                </>
              )}

           


              {/* 💳 NORMAL */}
              {step === 2 && type === "normal" && (
                <>
                  <h2 className="text-lg font-bold mb-4 text-gray-800">
                    Método de pago
                  </h2>

                  <select
                    name="paymentMethod"
                    value={form.paymentMethod}
                    onChange={handleChange}
                    className="w-full p-3 rounded-xl border border-gray-300 mb-4"
                  >
                    <option value="CASH">Efectivo</option>
                    <option value="CARD">Tarjeta</option>
                    <option value="NEQUI">Nequi</option>
                  </select>

                  {/* 💰 EFECTIVO */}
                  {form.paymentMethod === "CASH" && (
                    <div className="bg-gray-50 p-4 rounded-xl border space-y-3 mb-4">

                      <div>
                        <label className="text-xs text-gray-500">Total</label>
                       <input
                      value={formatCOP(total)}
                      readOnly
                      className="w-full p-2 rounded bg-gray-200 mt-1 font-semibold"
                    />
                        
                      </div>

                      <div>
                        <label className="text-xs text-gray-500">Recibido</label>
                        <input
                          type="text"
                          placeholder="Ej: 50,000"
                          value={cashData.received ? formatCOP(cashData.received) : ""}
                          onChange={(e) => {
                            const raw = e.target.value.replace(/\D/g, ""); // solo números
                            setCashData({ received: raw });
                          }}
                          className="w-full p-2 rounded border mt-1"
                        />
                      </div>

                      <div>
                        <label className="text-xs text-gray-500">Vueltas</label>
                        
                        <input
  value={formatCOP(change >= 0 ? change : 0)}
  readOnly
  className="w-full p-2 rounded bg-green-100 text-green-700 font-bold mt-1"
/>
                      </div>

                    </div>
                  )}

                  <button
                    onClick={handleSubmit}
                    className="w-full bg-gradient-to-r from-[#FF9F1C] to-[#FF4040] text-white py-3 rounded-xl shadow"
                  >
                    Finalizar venta
                  </button>
                </>
              )}

              {/* 🏢 EMPRESARIAL */}
              {step === 2 && type === "empresa" && (
                <>
                  <h2 className="text-lg font-bold mb-4 text-gray-800">
                    Datos del cliente
                  </h2>

                  <div className="flex flex-col gap-3">

                    <input name="clientName" placeholder="Empresa" onChange={handleChange} className="p-3 rounded border"/>
                    <input name="nit" placeholder="NIT" onChange={handleChange} className="p-3 rounded border"/>
                    <input name="email" placeholder="Correo" onChange={handleChange} className="p-3 rounded border"/>
                    <input name="address" placeholder="Dirección" onChange={handleChange} className="p-3 rounded border"/>
                    <input
  value={formatCOP(total)}readOnlyclassName="w-full p-2 rounded bg-gray-200 mt-1"
/>

                    <select
                      name="paymentMethod"
                      value={form.paymentMethod}
                      onChange={handleChange}
                      className="p-3 rounded border"
                    >
                      <option value="CASH">Efectivo</option>
                      <option value="CARD">Tarjeta</option>
                      <option value="NEQUI">Nequi</option>
                    </select>

                    {form.paymentMethod === "CASH" && (
                      <div className="bg-gray-50 p-4 rounded-xl border space-y-3">

                        <div>
                          <label className="text-xs text-gray-500">Total</label>
                          <input value={total} readOnly className="w-full p-2 rounded bg-gray-200 mt-1"/>
                        </div>

                        <div>
                          <label className="text-xs text-gray-500">Recibido</label>
                          <input
                            type="number"
                            value={cashData.received}
                            onChange={(e) =>
                              setCashData({ received: e.target.value })
                            }
                            className="w-full p-2 rounded border mt-1"
                          />
                        </div>

                        <div>
                          <label className="text-xs text-gray-500">Vueltas</label>
                          <input
                            value={change >= 0 ? change : 0}
                            readOnly
                            className="w-full p-2 rounded bg-green-100 text-green-700 font-bold mt-1"
                          />
                        </div>

                      </div>
                    )}

                    <button
                      onClick={handleSubmit}
                      className="bg-gradient-to-r from-[#FF9F1C] to-[#FF4040] text-white py-3 rounded-xl shadow"
                    >
                      Finalizar venta
                    </button>

                  </div>
                </>
              )}

              {/* ❌ */}
              <button
                onClick={onClose}
                className="mt-4 text-sm text-gray-400 hover:text-black w-full"
              >
                Cancelar
              </button>




{/* 🧾 STEP 3 → RECIBO */}
{step === 3 && receiptData && (
  <>
    <div className="flex flex-col items-center">

      <div
        id="receipt"
        className="bg-white w-[280px] p-4 text-[12px] font-mono text-black shadow-lg"
      >

        {/* HEADER */}
        <div className="text-center mb-2">

          <img
            src={logo}
            alt="logo"
            className="w-20 mb-2"
          />
          <p className="font-bold text-sm">DELISSA S.A.S</p>
          <p>NIT: 123456789</p>
          <p>Villavicencio - Meta</p>
          <p>Tel: 3000000000</p>
        </div>

        <hr className="border-dashed border-gray-400 my-2" />

        {/* INFO */}
        <div className="space-y-1">
          <div className="flex justify-between">
            <span>Fecha:</span>
            <span>{new Date().toLocaleString()}</span>
          </div>

          <div className="flex justify-between">
            <span>Pago:</span>
            <span>{receiptData.paymentMethod}</span>
          </div>
        </div>

        <hr className="border-dashed border-gray-400 my-2" />

        {/* PRODUCTOS (puedes conectar después) */}
        <div>

  <div className="flex justify-between font-bold text-[11px]">
    <span>Producto</span>
    <span>Total</span>
  </div>

  {receiptData.items.map((item, i) => (
    <div key={i} className="mb-1">

      <div className="flex justify-between">
        <span>{item.name}</span>
        <span>{formatCOP(item.price * item.qty)}</span>
      </div>

      <div className="flex justify-between text-[10px] text-gray-500">
        <span>
          {item.qty} x {formatCOP(item.price)}
        </span>
        <span></span>
      </div>

    </div>
  ))}

</div>

        <hr className="border-dashed border-gray-400 my-2" />

        {/* TOTALES */}
        <div className="space-y-1">

          <div className="flex justify-between">
            <span>Subtotal</span>
            <span>{formatCOP(receiptData.total)}</span>
          </div>

          <div className="flex justify-between font-bold text-lg">
            <span>Total:</span>
            <span>{formatCOP(receiptData.total)}</span>
          </div>

        </div>

        <hr className="border-dashed border-gray-400 my-2" />

        {/* PAGO */}
        <div className="space-y-1">

          <div className="flex justify-between">
            <span>Recibido:</span>
            <span>{formatCOP(receiptData.received)}</span>
          </div>

          <div className="flex justify-between font-bold">
            <span>Cambio:</span>
            <span>{formatCOP(receiptData.change)}</span>
          </div>

        </div>

        <hr className="border-dashed border-gray-400 my-2" />

        {/* FOOTER */}
        <div className="text-center text-[10px] mt-2">
          <p>Gracias por tu compra ❤️</p>
          <p>Vuelve pronto</p>
        </div>

      </div>

      {/* BOTONES */}
      <div className="flex gap-3 mt-4 w-full">

        <button
          onClick={() => window.print()}
          className="flex-1 bg-black text-white py-2 rounded-xl"
        >
          🖨️ Imprimir
        </button>

        <button
          onClick={() => {
            setStep(1);
            setType(null);
            setCashData({ received: "" });
            onClose();
          }}
          className="flex-1 bg-gray-200 py-2 rounded-xl"
        >
          Cerrar
        </button>

      </div>
    </div>
  </>
)}
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>,
    document.body
  );
}