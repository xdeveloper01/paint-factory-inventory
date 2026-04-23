import InventoryPage from './pages/InventoryPage';

function App() {
  return (
    <div className="min-h-screen bg-slate-50 flex">
      {/* Minimal Sidebar Placeholder */}
      <aside className="w-64 bg-slate-900 text-slate-300 p-6 flex flex-col gap-4">
        <div className="text-xl font-bold text-white mb-8 border-b border-slate-700 pb-4">
          PAINT FACTORY
        </div>
        <nav className="flex flex-col gap-2">
          <a href="#" className="px-4 py-2 hover:bg-slate-800 rounded-md">Dashboard</a>
          <a href="#" className="px-4 py-2 bg-slate-800 text-white rounded-md font-medium">Inventory</a>
          <a href="#" className="px-4 py-2 hover:bg-slate-800 rounded-md">Production</a>
          <a href="#" className="px-4 py-2 hover:bg-slate-800 rounded-md">Stock Movements</a>
        </nav>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 flex flex-col">
        {/* Minimal Header */}
        <header className="h-16 bg-white border-b flex items-center px-8">
          <span className="text-sm font-medium text-slate-500">Warehouse Operations</span>
        </header>

        {/* Page Content */}
        <div className="flex-1 overflow-auto">
          <InventoryPage />
        </div>
      </main>
    </div>
  );
}

export default App;
