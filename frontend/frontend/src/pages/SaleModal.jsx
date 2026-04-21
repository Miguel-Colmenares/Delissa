import { motion, AnimatePresence } from "framer-motion";
import { useState, useEffect } from "react";
import { createPortal } from "react-dom";

export default function SaleModal({ open, onClose, onConfirm }) {

  const [step, setStep] = useState(1);
  const [type, setType] = useState(null);

  const [form, setForm] = useState({
    paymentMethod: "CASH",
    clientName: "",
    nit: "",
    email: "",
    address: ""
  });

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

    onClose();
    setStep(1);
    setType(null);
  };

  return createPortal(
    <AnimatePresence>
      {open && (
        <>
          {/* 🔥 FONDO BLUR */}
          <motion.div
            className="fixed inset-0 bg-black/50 backdrop-blur-lg z-[99999]"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          />

          {/* 🔥 MODAL CENTRADO PEQUEÑO */}
          <motion.div
            className="fixed inset-0 z-[99999] flex items-center justify-center"
            initial={{ opacity: 0, scale: 0.8, y: 40 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.8, y: 40 }}
            transition={{ duration: 0.25 }}
          >
            <div className="bg-[#1c1c1c]/95 backdrop-blur-xl text-white rounded-2xl shadow-2xl p-6 w-[380px] border border-white/10">

              {/* 🔙 VOLVER */}
              {step === 2 && (
                <button
                  onClick={handleBack}
                  className="text-sm text-gray-400 hover:text-white mb-3"
                >
                  ← Volver
                </button>
              )}

              {/* 🧾 STEP 1 */}
              {step === 1 && (
                <>
                  <h2 className="text-lg font-bold mb-5 text-center">
                    Tipo de factura
                  </h2>

                  <div className="flex flex-col gap-3">
                    <button
                      onClick={() => handleSelectType("normal")}
                      className="bg-gradient-to-r from-[#FF9F1C] to-[#FF4040] py-3 rounded-xl shadow hover:scale-[1.02] transition"
                    >
                      🧾 Factura Normal
                    </button>

                    <button
                      onClick={() => handleSelectType("empresa")}
                      className="bg-gray-900 py-3 rounded-xl shadow hover:scale-[1.02] transition"
                    >
                      🏢 Factura Empresarial
                    </button>
                  </div>
                </>
              )}

              {/* 💳 NORMAL */}
              {step === 2 && type === "normal" && (
                <>
                  <h2 className="text-md font-bold mb-4 text-center">
                    Método de pago
                  </h2>

                  <select
                    name="paymentMethod"
                    value={form.paymentMethod}
                    onChange={handleChange}
                    className="w-full p-2 rounded-xl mb-4 text-black"
                  >
                    <option value="CASH">Efectivo</option>
                    <option value="CARD">Tarjeta</option>
                    <option value="NEQUI">Nequi</option>
                  </select>

                  <button
                    onClick={handleSubmit}
                    className="w-full bg-[#FF9F1C] py-2 rounded-xl hover:bg-[#e88d10]"
                  >
                    Finalizar
                  </button>
                </>
              )}

              {/* 🏢 EMPRESARIAL */}
              {step === 2 && type === "empresa" && (
                <>
                  <h2 className="text-md font-bold mb-3 text-center">
                    Datos del cliente
                  </h2>

                  <div className="flex flex-col gap-2">

                    <input name="clientName" placeholder="Empresa" onChange={handleChange} className="p-2 rounded text-black"/>
                    <input name="nit" placeholder="NIT" onChange={handleChange} className="p-2 rounded text-black"/>
                    <input name="email" placeholder="Correo" onChange={handleChange} className="p-2 rounded text-black"/>
                    <input name="address" placeholder="Dirección" onChange={handleChange} className="p-2 rounded text-black"/>
                    <input
  type="file"
  onChange={(e)=>setImageFile(e.target.files[0])}
  className="border p-2 rounded"
/>
                    <select
                      name="paymentMethod"
                      value={form.paymentMethod}
                      onChange={handleChange}
                      className="p-2 rounded text-black"
                    >
                      <option value="CASH">Efectivo</option>
                      <option value="CARD">Tarjeta</option>
                      <option value="NEQUI">Nequi</option>
                    </select>

                    <button
                      onClick={handleSubmit}
                      className="bg-gradient-to-r from-[#FF9F1C] to-[#FF4040] py-2 rounded-xl mt-2"
                    >
                      Finalizar
                    </button>

                  </div>
                </>
              )}

              {/* ❌ CANCELAR */}
              <button
                onClick={onClose}
                className="mt-4 text-xs text-gray-400 hover:text-white w-full"
              >
                Cancelar
              </button>

            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>,
    document.body
  );
}