import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Plus,
  Minus,
  ShoppingCart,
  Trash2,
  ChevronRight,
  Utensils,
  LayoutDashboard,
  Package,
  BarChart3,
  FileText,
  Search,
  User,
  LogOut,
} from "lucide-react";
import { useNavigate } from "react-router-dom";

// --- PRODUCTOS MOCK ---
const PRODUCTS = [
  { id: 1, name: "Burger Doble", price: 12000, img: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300" },
  { id: 2, name: "Papas Fritas", price: 4000, img: "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=300" },
  { id: 3, name: "Hot Dog", price: 7500, img: "https://images.unsplash.com/photo-1541214113241-21578d2d9b62?w=300" },
  { id: 4, name: "Nuggets", price: 9000, img: "https://images.unsplash.com/photo-1562967914-608f82629710?w=300" },
];

export default function Dashboard() {
  const [cart, setCart] = useState([]);
  const [activeTab, setActiveTab] = useState("ventas");
  const [openMenu, setOpenMenu] = useState(false);

  const navigate = useNavigate();

  // --- CARRITO ---
  const addToCart = (product) => {
    setCart((prev) => {
      const exists = prev.find((i) => i.id === product.id);
      if (exists)
        return prev.map((i) =>
          i.id === product.id ? { ...i, qty: i.qty + 1 } : i
        );
      return [...prev, { ...product, qty: 1 }];
    });
  };

  const updateQty = (id, delta) => {
    setCart((prev) =>
      prev.map((i) =>
        i.id === id ? { ...i, qty: Math.max(1, i.qty + delta) } : i
      )
    );
  };

  const removeFromCart = (id) =>
    setCart((prev) => prev.filter((i) => i.id !== id));

  const subtotal = cart.reduce((acc, i) => acc + i.price * i.qty, 0);

  // --- ACCIONES ---
  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/");
  };

  const goProfile = () => {
    alert("Perfil próximamente 🔥");
  };

  const formatCOP = (value) => {
  return new Intl.NumberFormat("es-CO", {
    style: "currency",
    currency: "COP",
    minimumFractionDigits: 0,
  }).format(value);
};

  return (
    <div className="w-screen h-screen flex overflow-hidden bg-white">

      {/* SIDEBAR */}
      <nav className="w-20 lg:w-64 bg-[#FF9F1C] flex flex-col items-center py-8 text-white">
        <Utensils size={40} className="bg-white text-[#FF9F1C] p-2 rounded-2xl mb-10" />

        <div className="flex flex-col gap-4 w-full px-3">
          <SidebarItem icon={<LayoutDashboard />} label="Dash" active={activeTab==="dash"} onClick={()=>setActiveTab("dash")} />
          <SidebarItem icon={<ShoppingCart />} label="Ventas" active={activeTab==="ventas"} onClick={()=>setActiveTab("ventas")} />
          <SidebarItem icon={<Package />} label="Stock" active={activeTab==="stock"} onClick={()=>setActiveTab("stock")} />
          <SidebarItem icon={<BarChart3 />} label="Stats" active={activeTab==="stats"} onClick={()=>setActiveTab("stats")} />
          <SidebarItem icon={<FileText />} label="Facturas" active={activeTab==="facturas"} onClick={()=>setActiveTab("facturas")} />
        </div>
      </nav>

      {/* PRODUCTOS */}
      <section className="flex-1 bg-gray-50 p-6 overflow-y-auto">
        <header className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-black">
            MENÚ <span className="text-[#FF4040]">RÁPIDO</span>
          </h1>

          <div className="relative w-64">
            <Search className="absolute left-3 top-3 text-gray-400" size={18} />
            <input
              placeholder="Buscar..."
              className="w-full pl-9 pr-3 py-2 rounded-xl border shadow-sm outline-none focus:ring-2 focus:ring-[#FFBF00]"
            />
          </div>
        </header>

        <div className="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-5">
          {PRODUCTS.map((p) => (
            <motion.div
              key={p.id}
              whileHover={{ y: -5 }}
              className="bg-white p-3 rounded-2xl shadow hover:shadow-xl transition"
            >
              <img src={p.img} className="h-28 w-full object-cover rounded-xl mb-2" />
              <h3 className="text-sm font-bold text-center">{p.name}</h3>
              <p className="text-center text-[#FF4040] font-bold mb-2">
              {formatCOP(p.price)}
              </p>

              <div className="flex justify-center gap-2">
                <button
                  onClick={() => addToCart(p)}
                  className="bg-[#FF9F1C] text-white px-3 py-1 rounded-lg hover:bg-[#e88d10]"
                >
                  <Plus size={16} />
                </button>
              </div>
            </motion.div>
          ))}
        </div>
      </section>

      {/* CARRITO */}
      <aside className="w-[350px] bg-white border-l flex flex-col">
        <div className="p-6 bg-[#FF4040] text-white">
          <h2 className="flex items-center gap-2 font-bold">
            <ShoppingCart /> Orden
          </h2>
        </div>

        <div className="flex-1 overflow-y-auto p-4 space-y-3">
          <AnimatePresence>
            {cart.map((item) => (
              <motion.div
                key={item.id}
                layout
                initial={{ opacity: 0, x: 30 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0 }}
                className="flex justify-between items-center bg-gray-100 p-3 rounded-xl"
              >
                <div>
                  <p className="font-bold text-sm">{item.name}</p>
                  <p className="text-xs">{formatCOP(item.price * item.qty)}</p>
                </div>

                <div className="flex items-center gap-2">
                  <button onClick={()=>updateQty(item.id,-1)}><Minus size={14}/></button>
                  <span>{item.qty}</span>
                  <button onClick={()=>updateQty(item.id,1)}><Plus size={14}/></button>
                  <button onClick={()=>removeFromCart(item.id)}><Trash2 size={14}/></button>
                </div>
              </motion.div>
            ))}
          </AnimatePresence>

          {cart.length === 0 && (
            <div className="text-center text-gray-400 mt-20">
              Sin productos
            </div>
          )}
        </div>

        <div className="p-4 border-t">
          <h3 className="font-bold mb-2">Total: ${formatCOP(subtotal)}</h3>

          <button className="w-full bg-[#FF9F1C] text-white py-3 rounded-xl hover:bg-[#e88d10]">
            Finalizar Venta
          </button>
        </div>
      </aside>

      {/* 🔥 BOTÓN FLOTANTE */}
      <div className="fixed bottom-6 lefts-6 z-50">

        <AnimatePresence>
          {openMenu && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: 20 }}
              className="flex flex-col gap-3 mb-3 items-end"
            >
              <button
                onClick={goProfile}
                className="flex items-center gap-2 bg-white shadow-lg px-4 py-2 rounded-xl hover:bg-gray-100"
              >
                <User size={18} /> Perfil
              </button>

              <button
                onClick={handleLogout}
                className="flex items-center gap-2 bg-red-500 text-white shadow-lg px-4 py-2 rounded-xl hover:bg-red-600"
              >
                <LogOut size={18} /> Cerrar sesión
              </button>
            </motion.div>
          )}
        </AnimatePresence>

        <motion.button
          whileTap={{ scale: 0.9 }}
          onClick={() => setOpenMenu(!openMenu)}
          className="w-14 h-14 bg-[#f57c00] hover:bg-[#d35400] text-white rounded-full shadow-xl flex items-center justify-center text-xl transition"    
        >
          ⚙️
        </motion.button>
      </div>

    </div>
  );
}

// --- SIDEBAR ITEM ---
function SidebarItem({ icon, label, active, onClick }) {
  return (
    <button
      onClick={onClick}
      className={`flex items-center gap-3 p-3 rounded-xl ${
        active ? "bg-white text-[#FF9F1C]" : "text-white/80 hover:bg-orange-400"
      }`}
    >
      {icon}
      <span className="hidden lg:block">{label}</span>
    </button>
  );
}