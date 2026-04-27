import React, { useState, useEffect } from "react";

const API = "http://localhost:8080";

export default function Box_Money({ formatCOP }) {
  const [sales, setSales] = useState([]);
  const [base, setBase] = useState("");
  const [baseNumber, setBaseNumber] = useState(0);

  const [expenses, setExpenses] = useState([]);
  const [expenseInput, setExpenseInput] = useState("");

  const [realCash, setRealCash] = useState("");
  const [showCashInput, setShowCashInput] = useState(false);

  const today = new Date().toISOString().split("T")[0];

  useEffect(() => {
    fetchSales();
    fetchExpenses();
  }, []);

  const fetchSales = async () => {
    try {
      const res = await fetch(`${API}/sales`);
      const data = await res.json();

      const safeData = Array.isArray(data) ? data : [];

      const filtered = safeData.filter(
        (s) => new Date(s.date).toISOString().split("T")[0] === today
      );

      setSales(filtered);
    } catch {
      setSales([]);
    }
  };

  const fetchExpenses = async () => {
    try {
      const res = await fetch(`${API}/expenses/today`);
      const data = await res.json();
      setExpenses(Array.isArray(data) ? data : []);
    } catch {
      setExpenses([]);
    }
  };

  const handleBaseChange = (e) => {
    const raw = e.target.value.replace(/\D/g, "");
    setBase(raw);
    setBaseNumber(Number(raw));
  };

  const handleRealCash = (e) => {
    const raw = e.target.value.replace(/\D/g, "");
    setRealCash(raw);
  };

  // 🔥 CALCULOS
  const totalSales = sales.reduce((acc, s) => acc + (s.total || 0), 0);

  const cash = sales
    .filter((s) => s.paymentMethod === "CASH")
    .reduce((acc, s) => acc + (s.total || 0), 0);

  const nequi = sales
    .filter((s) => s.paymentMethod === "NEQUI")
    .reduce((acc, s) => acc + (s.total || 0), 0);

  const card = sales
    .filter((s) => s.paymentMethod === "CARD")
    .reduce((acc, s) => acc + (s.total || 0), 0);

  const totalExpenses = expenses.reduce(
    (acc, e) => acc + (e.amount || 0),
    0
  );

  const expectedCash = baseNumber + cash - totalExpenses;
  const realCashNumber = Number(realCash || 0);
  const difference = realCashNumber - expectedCash;

  // 🔥 PRODUCTOS
  const productSummary = {};

  sales.forEach((sale) => {
    sale.details?.forEach((item) => {
      const name = item.productName || "Producto";

      if (!productSummary[name]) productSummary[name] = 0;

      productSummary[name] += item.quantity || 0;
    });
  });

  const sortedProducts = Object.entries(productSummary).sort(
    (a, b) => b[1] - a[1]
  );

  const totalProducts = sortedProducts.reduce((acc, p) => acc + p[1], 0);
  const totalTransactions = sales.length;
  const avgTicket =
    totalTransactions > 0 ? totalSales / totalTransactions : 0;

  // 🔥 AGREGAR GASTO
  const addExpense = async () => {
    if (!expenseInput) return;

    await fetch(`${API}/expenses`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        amount: Number(expenseInput),
        date: new Date(),
      }),
    });

    setExpenseInput("");
    fetchExpenses();
  };

  // 🔥 ELIMINAR GASTO
  const deleteExpense = async (id) => {
    try {
      await fetch(`${API}/expenses/${id}`, {
        method: "DELETE",
      });
      fetchExpenses();
    } catch {
      alert("Error eliminando gasto");
    }
  };

  // 🔥 IMPRIMIR
  const handlePrint = () => window.print();

  // 🔥 GUARDAR
  const saveClosure = () => {
    const closure = {
      date: today,
      totalSales,
      cash,
      nequi,
      card,
      expenses: totalExpenses,
      expectedCash,
      realCash: realCashNumber,
      difference,
    };

    const history =
      JSON.parse(localStorage.getItem("closures")) || [];

    history.push(closure);
    localStorage.setItem("closures", JSON.stringify(history));

    alert("✅ Cierre guardado");
  };

  return (
    <div className="p-6 grid grid-cols-1 lg:grid-cols-3 gap-6">

      {/* IZQUIERDA */}
      <div className="lg:col-span-2 space-y-6">

        <h2 className="text-2xl font-bold text-gray-700">
          Cierre de Caja 💰
        </h2>

        {/* BASE */}
        <div className="bg-white p-4 rounded-xl shadow">
          <label className="text-sm text-gray-500">
            Base inicial del día
          </label>

          <input
            type="text"
            value={base ? formatCOP(base) : ""}
            onChange={handleBaseChange}
            className="w-full mt-2 p-3 border rounded-xl bg-white text-black"
          />
        </div>

        {/* RESUMEN */}
        <div className="grid grid-cols-2 gap-4">
          <Card title="Ventas" value={formatCOP(totalSales)} />
          <Card title="Efectivo" value={formatCOP(cash)} />
          <Card title="Nequi" value={formatCOP(nequi)} />
          <Card title="Tarjeta" value={formatCOP(card)} />
        </div>

        {/* GASTOS */}
        <div className="bg-white p-4 rounded-xl shadow">
          <h3 className="font-bold mb-2">Gastos</h3>

          <div className="flex gap-2 mb-3">
            <input
              type="number"
              value={expenseInput}
              onChange={(e) => setExpenseInput(e.target.value)}
              className="flex-1 p-2 border rounded"
              placeholder="Agregar gasto"
            />
            <button
              onClick={addExpense}
              className="bg-red-500 text-white px-4 rounded"
            >
              +
            </button>
          </div>

          {/* 🔥 LISTA GASTOS */}
          <div className="space-y-1">
            {expenses.map((e) => (
              <div
                key={e.id}
                className="flex justify-between items-center text-sm bg-gray-100 p-2 rounded"
              >
                <span>{formatCOP(e.amount)}</span>

                <button
                  onClick={() => deleteExpense(e.id)}
                  className="text-red-500"
                >
                  🗑️
                </button>
              </div>
            ))}
          </div>

          <p className="text-sm mt-2 text-gray-500">
            Total: {formatCOP(totalExpenses)}
          </p>
        </div>

        {/* 💵 BOTON CONTAR CAJA */}
        {!showCashInput && (
          <button
            onClick={() => setShowCashInput(true)}
            className="w-full bg-blue-500 text-white py-3 rounded-xl"
          >
            💵 Contar caja
          </button>
        )}

        {/* INPUT SOLO SI SE ACTIVA */}
        {showCashInput && (
          <div className="bg-white p-4 rounded-xl shadow">
            <label className="text-sm text-gray-500">
              Dinero contado
            </label>

            <input
              type="text"
              value={realCash ? formatCOP(realCash) : ""}
              onChange={handleRealCash}
              className="w-full mt-2 p-3 border rounded"
            />
          </div>
        )}

        {/* RESULTADO */}
        <div className={`p-5 rounded-xl shadow ${
          difference === 0 ? "bg-green-100" : "bg-red-100"
        }`}>
          <p className="font-bold">
            Esperado: {formatCOP(expectedCash)}
          </p>

          {showCashInput && (
            <>
              <p className="font-bold">
                Diferencia: {formatCOP(difference)}
              </p>

              {difference !== 0 && (
                <p className="text-red-600 text-sm">
                  ⚠️ Caja descuadrada
                </p>
              )}
            </>
          )}
        </div>

        {/* BOTONES */}
        <div className="flex gap-3">
          <button
            onClick={handlePrint}
            className="flex-1 bg-black text-white py-3 rounded-xl"
          >
            🖨️ Imprimir
          </button>

          <button
            onClick={saveClosure}
            className="flex-1 bg-green-600 text-white py-3 rounded-xl"
          >
            💾 Guardar
          </button>
        </div>

      </div>

      {/* DERECHA */}
      <div className="space-y-4">

        <StatCard title="Productos" value={totalProducts} />
        <StatCard title="Ventas" value={totalTransactions} />
        <StatCard title="Ticket" value={formatCOP(avgTicket)} />

        <div className="bg-white p-4 rounded-xl shadow max-h-[300px] overflow-y-auto">
          <h3 className="font-bold mb-3">Productos vendidos</h3>

          {sortedProducts.map(([name, qty], i) => (
            <div key={i} className="flex justify-between text-sm border-b py-1">
              <span>{name}</span>
              <span className="font-bold">{qty}</span>
            </div>
          ))}
        </div>

      </div>
    </div>
  );
}

function Card({ title, value }) {
  return (
    <div className="bg-white p-4 rounded-xl shadow">
      <p className="text-sm text-gray-500">{title}</p>
      <p className="text-xl font-bold">{value}</p>
    </div>
  );
}

function StatCard({ title, value }) {
  return (
    <div className="bg-gradient-to-r from-[#FF9F1C] to-[#FF4040] text-white p-4 rounded-xl shadow">
      <p className="text-sm opacity-80">{title}</p>
      <p className="text-2xl font-bold">{value}</p>
    </div>
  );
}