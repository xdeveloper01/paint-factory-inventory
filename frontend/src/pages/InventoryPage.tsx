import { useState } from 'react';
import { 
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow 
} from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { 
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger, DialogFooter
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { ArrowDownToLine, ArrowUpFromLine } from 'lucide-react';

// Production Mock Data
const inventoryData = [
  { id: '1', name: 'Titanium Dioxide', qty: 2500, unit: 'kg', warehouse: 'WH-Alpha', status: 'OK' },
  { id: '2', name: 'Acrylic Resin', qty: 400, unit: 'Liters', warehouse: 'WH-Beta', status: 'LOW' },
  { id: '3', name: 'Xylene Solvent', qty: 50, unit: 'Liters', warehouse: 'WH-Alpha', status: 'CRITICAL' },
];

export default function InventoryPage() {
  const [selectedItem, setSelectedItem] = useState<any>(null);

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'OK': return <Badge variant="default" className="bg-emerald-600 hover:bg-emerald-700">OK</Badge>;
      case 'LOW': return <Badge variant="secondary" className="bg-amber-500 text-white hover:bg-amber-600">LOW</Badge>;
      case 'CRITICAL': return <Badge variant="destructive">CRITICAL</Badge>;
      default: return <Badge variant="outline">{status}</Badge>;
    }
  };

  return (
    <div className="p-8 max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-semibold tracking-tight text-slate-900">Inventory Management</h1>
      </div>

      <div className="border rounded-md bg-white shadow-sm">
        <Table>
          <TableHeader>
            <TableRow className="bg-slate-50/50">
              <TableHead>Material Name</TableHead>
              <TableHead className="text-right">Quantity</TableHead>
              <TableHead>Unit</TableHead>
              <TableHead>Warehouse</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {inventoryData.map((item) => (
              <TableRow key={item.id}>
                <TableCell className="font-medium text-slate-900">{item.name}</TableCell>
                <TableCell className="text-right tabular-nums">{item.qty.toLocaleString()}</TableCell>
                <TableCell className="text-slate-500">{item.unit}</TableCell>
                <TableCell className="text-slate-600">{item.warehouse}</TableCell>
                <TableCell>{getStatusBadge(item.status)}</TableCell>
                <TableCell className="text-right space-x-2">
                  <Dialog>
                    <DialogTrigger asChild>
                      <Button variant="outline" size="sm" onClick={() => setSelectedItem(item)}>
                        <ArrowDownToLine className="w-4 h-4 mr-2 text-emerald-600" /> IN
                      </Button>
                    </DialogTrigger>
                    <RecordMovementModal item={selectedItem} type="IN" />
                  </Dialog>
                  
                  <Dialog>
                    <DialogTrigger asChild>
                      <Button variant="outline" size="sm" onClick={() => setSelectedItem(item)}>
                        <ArrowUpFromLine className="w-4 h-4 mr-2 text-amber-600" /> OUT
                      </Button>
                    </DialogTrigger>
                    <RecordMovementModal item={selectedItem} type="OUT" />
                  </Dialog>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}

function RecordMovementModal({ item, type }: { item: any, type: 'IN' | 'OUT' }) {
  if (!item) return null;
  return (
    <DialogContent className="sm:max-w-[425px]">
      <DialogHeader>
        <DialogTitle className={type === 'IN' ? 'text-emerald-700' : 'text-amber-700'}>
          Record Stock {type}
        </DialogTitle>
      </DialogHeader>
      <div className="grid gap-4 py-4">
        <div className="grid grid-cols-4 items-center gap-4">
          <Label className="text-right text-slate-500">Material</Label>
          <span className="col-span-3 font-medium text-slate-900">{item.name}</span>
        </div>
        <div className="grid grid-cols-4 items-center gap-4">
          <Label htmlFor="qty" className="text-right text-slate-500">Quantity</Label>
          <div className="col-span-3 flex items-center gap-2">
            <Input id="qty" type="number" placeholder="0.00" className="w-full font-mono" />
            <span className="text-sm font-medium text-slate-500 w-12">{item.unit}</span>
          </div>
        </div>
      </div>
      <DialogFooter>
        <Button type="submit" variant={type === 'IN' ? 'default' : 'secondary'} className={type === 'IN' ? 'bg-emerald-600 hover:bg-emerald-700' : ''}>
          Confirm {type}
        </Button>
      </DialogFooter>
    </DialogContent>
  );
}
