"use client";

import { useRouter } from "next/navigation";
import { useAuth } from "@/lib/auth-context";
import { useState } from "react";
import Image from "next/image";
import TICEmergencyModal from "@/components/TICEmergencyModal";

interface EmergencyAlert {
  id: string;
  alertId: string;
  type: "TIC" | "Medical" | "Vehicle Breakdown" | "Natural Disaster" | "Equipment Failure";
  severity: "Critical" | "High" | "Medium" | "Low";
  status: "Active" | "Responding" | "Resolved" | "Closed";
  truck: string;
  personnel: string;
  location: { lat: number; lng: number; label: string };
  description: string;
  timestamp: Date;
  resolvedAt?: Date;
  casualties?: number;
  hostilesCount?: string;
}

const DUMMY_ALERTS: EmergencyAlert[] = [
  {
    id: "1",
    alertId: "EMG-2026-001",
    type: "TIC",
    severity: "Critical",
    status: "Active",
    truck: "TRUCK-07",
    personnel: "SGT. Rodriguez",
    location: { lat: 9.8236214, lng: 118.725328, label: "Barangay Tagburos, Puerto Princesa" },
    description: "TIC! Under fire! Ambushed on route. Requesting immediate QRF support. Estimated 5-7 hostiles with small arms fire.",
    timestamp: new Date("2026-03-05T14:30:00"),
    casualties: 0,
    hostilesCount: "5-7",
  },
  {
    id: "2",
    alertId: "EMG-2026-002",
    type: "Medical",
    severity: "High",
    status: "Responding",
    truck: "TRUCK-03",
    personnel: "CPL. Santos",
    location: { lat: 9.4705341, lng: 118.5560033, label: "Narra, Palawan" },
    description: "Medical emergency. One personnel injured during supply unloading. Requires immediate medical evacuation.",
    timestamp: new Date("2026-03-05T12:15:00"),
    casualties: 1,
  },
  {
    id: "3",
    alertId: "EMG-2026-003",
    type: "Vehicle Breakdown",
    severity: "Medium",
    status: "Resolved",
    truck: "TRUCK-05",
    personnel: "SGT. Dela Cruz",
    location: { lat: 8.7598513, lng: 117.608354, label: "Quezon, Palawan" },
    description: "Engine failure in transit. Vehicle immobilized but crew safe. Maintenance team dispatched.",
    timestamp: new Date("2026-03-04T16:45:00"),
    resolvedAt: new Date("2026-03-04T19:20:00"),
    casualties: 0,
  },
  {
    id: "4",
    alertId: "EMG-2026-004",
    type: "TIC",
    severity: "Critical",
    status: "Resolved",
    truck: "TRUCK-02",
    personnel: "LT. Reyes",
    location: { lat: 8.361528, lng: 117.1898946, label: "Brooke's Point, Palawan" },
    description: "Troops in contact with hostile elements. Heavy exchange of fire. QRF deployed and situation neutralized.",
    timestamp: new Date("2026-03-03T09:20:00"),
    resolvedAt: new Date("2026-03-03T11:45:00"),
    casualties: 2,
    hostilesCount: "8-10",
  },
  {
    id: "5",
    alertId: "EMG-2026-005",
    type: "Natural Disaster",
    severity: "High",
    status: "Closed",
    truck: "TRUCK-08",
    personnel: "SGT. Garciano",
    location: { lat: 11.1050771, lng: 119.4691487, label: "Coron, Palawan" },
    description: "Flash flood blocking route. Personnel evacuated to high ground. All safe. Road cleared after 6 hours.",
    timestamp: new Date("2026-03-02T05:30:00"),
    resolvedAt: new Date("2026-03-02T11:30:00"),
    casualties: 0,
  },
  {
    id: "6",
    alertId: "EMG-2026-006",
    type: "Equipment Failure",
    severity: "Low",
    status: "Closed",
    truck: "TRUCK-01",
    personnel: "CPL. Magno",
    location: { lat: 9.8013701, lng: 118.749166, label: "San Rafael, Puerto Princesa" },
    description: "Communication equipment malfunction. Unable to maintain radio contact. Backup systems activated.",
    timestamp: new Date("2026-03-01T14:00:00"),
    resolvedAt: new Date("2026-03-01T15:30:00"),
    casualties: 0,
  },
];

const SEVERITY_STYLES: Record<string, string> = {
  Critical: "bg-rose-100 text-rose-700 border-rose-300",
  High: "bg-orange-100 text-orange-700 border-orange-300",
  Medium: "bg-amber-100 text-amber-700 border-amber-300",
  Low: "bg-blue-100 text-blue-700 border-blue-300",
};

const STATUS_STYLES: Record<string, string> = {
  Active: "bg-red-100 text-red-700 border-red-300",
  Responding: "bg-violet-100 text-violet-700 border-violet-300",
  Resolved: "bg-emerald-100 text-emerald-700 border-emerald-300",
  Closed: "bg-slate-100 text-slate-700 border-slate-300",
};

const TYPE_ICONS: Record<string, { icon: string; color: string }> = {
  TIC: { icon: "emergency", color: "text-red-600" },
  Medical: { icon: "medical_services", color: "text-rose-600" },
  "Vehicle Breakdown": { icon: "car_crash", color: "text-orange-600" },
  "Natural Disaster": { icon: "thunderstorm", color: "text-blue-600" },
  "Equipment Failure": { icon: "report_problem", color: "text-amber-600" },
};

export default function EmergencyAlerts() {
  const { user, loading, signOut } = useAuth();
  const router = useRouter();
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [selectedAlert, setSelectedAlert] = useState<EmergencyAlert | null>(null);
  const [filterStatus, setFilterStatus] = useState<string>("All");
  const [filterSeverity, setFilterSeverity] = useState<string>("All");

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center bg-gradient-to-br from-slate-900 to-slate-800">
        <div className="text-center">
          <span className="material-symbols-outlined animate-spin text-blue-400" style={{ fontSize: "3rem" }}>
            progress_activity
          </span>
          <p className="mt-4 text-slate-300 font-medium tracking-wide">Loading...</p>
        </div>
      </div>
    );
  }

  if (!user) {
    router.push("/login");
    return null;
  }

  const handleLogout = async () => {
    await signOut();
    router.push("/login");
  };

  const navigationItems = [
    { name: "Dashboard", icon: "dashboard", href: "/dashboard", active: false },
    { name: "Personnels", icon: "groups", href: "/personnels", active: false },
    { name: "Vehicle", icon: "local_shipping", href: "/vehicle", active: false },
    { name: "Emergency Alerts", icon: "emergency", href: "/emergency-alerts", active: true },
    { name: "History", icon: "history", href: "/history", active: false },
  ];

  const filteredAlerts = DUMMY_ALERTS.filter((alert) => {
    const statusMatch = filterStatus === "All" || alert.status === filterStatus;
    const severityMatch = filterSeverity === "All" || alert.severity === filterSeverity;
    return statusMatch && severityMatch;
  });

  const formatTimestamp = (date: Date): string => {
    return date.toLocaleString("en-PH", {
      month: "short",
      day: "numeric",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const getTimeElapsed = (start: Date, end?: Date): string => {
    const endTime = end || new Date();
    const diffMs = endTime.getTime() - start.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    
    if (diffMins < 60) return `${diffMins}m`;
    if (diffHours < 24) return `${diffHours}h ${diffMins % 60}m`;
    const diffDays = Math.floor(diffHours / 24);
    return `${diffDays}d ${diffHours % 24}h`;
  };

  // Calculate stats
  const stats = {
    active: DUMMY_ALERTS.filter(a => a.status === "Active").length,
    responding: DUMMY_ALERTS.filter(a => a.status === "Responding").length,
    resolved: DUMMY_ALERTS.filter(a => a.status === "Resolved").length,
    totalCasualties: DUMMY_ALERTS.reduce((sum, a) => sum + (a.casualties || 0), 0),
  };

  return (
    <div className="flex h-screen bg-gradient-to-br from-slate-100 to-slate-200">
      {/* Emergency Detail Modal */}
      {selectedAlert && selectedAlert.status === "Active" && selectedAlert.type === "TIC" && (
        <TICEmergencyModal
          onClose={() => setSelectedAlert(null)}
          truckCodename={selectedAlert.truck}
          personnelName={selectedAlert.personnel}
        />
      )}

      {/* Sidebar */}
      <div
        className={`${
          sidebarOpen ? "w-64" : "w-20"
        } bg-gradient-to-b from-slate-900 to-slate-800 shadow-2xl transition-all duration-300 ease-in-out flex flex-col border-r border-slate-700/50`}
      >
        {/* Logo */}
        <div className={`flex h-16 items-center border-b border-slate-700/50 px-3 ${sidebarOpen ? 'justify-between' : 'justify-center'}`}>
          <div className="flex items-center gap-3 min-w-0">
            <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-white shadow-lg flex-shrink-0 overflow-hidden">
              <Image
                src="/logo.png"
                alt="2nd JLSU Logo"
                width={44}
                height={44}
                className="object-contain"
              />
            </div>
            {sidebarOpen && (
              <div className="animate-fade-in overflow-hidden">
                <p className="font-bold text-white tracking-wide text-lg">2nd JLSU</p>
                <p className="text-xs text-slate-400">Log Truck System</p>
              </div>
            )}
          </div>
          {sidebarOpen && (
            <button
              onClick={() => setSidebarOpen(!sidebarOpen)}
              className="rounded-lg p-1.5 hover:bg-slate-700 transition-colors text-slate-400 hover:text-white flex-shrink-0"
            >
              <span className="material-symbols-outlined" style={{ fontSize: "1.25rem" }}>menu_open</span>
            </button>
          )}
        </div>
        {!sidebarOpen && (
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="flex items-center justify-center w-full py-2 hover:bg-slate-700 transition-colors text-slate-400 hover:text-white border-b border-slate-700/50"
          >
            <span className="material-symbols-outlined" style={{ fontSize: "1.25rem" }}>menu</span>
          </button>
        )}

        {/* Nav */}
        <nav className="space-y-1 px-3 py-4 flex-1">
          {navigationItems.map((item) => (
            <a
              key={item.name}
              href={item.href}
              className={`flex items-center rounded-xl transition-all duration-200 ${
                sidebarOpen ? "gap-3 px-4 py-4" : "justify-center px-2 py-4"
              } ${
                item.active
                  ? "bg-gradient-to-r from-rose-500/20 to-rose-500/5 text-rose-400 border border-rose-500/30 shadow-md"
                  : "text-slate-400 hover:bg-slate-700/50 hover:text-white"
              }`}
            >
              <span className="material-symbols-outlined flex-shrink-0" style={{ fontSize: "1.5rem" }}>{item.icon}</span>
              {sidebarOpen && <span className="truncate text-sm font-semibold">{item.name}</span>}
            </a>
          ))}
        </nav>

        {/* Logout */}
        <div className="border-t border-slate-700/50 p-3">
          <button
            onClick={handleLogout}
            className={`flex w-full items-center rounded-xl py-4 text-slate-400 hover:bg-rose-500/10 hover:text-rose-400 transition-all duration-200 border border-transparent hover:border-rose-500/20 ${
              sidebarOpen ? 'gap-3 px-4' : 'justify-center px-2'
            }`}
          >
            <span className="material-symbols-outlined flex-shrink-0" style={{ fontSize: "1.5rem" }}>logout</span>
            {sidebarOpen && <span className="text-sm font-semibold">Logout</span>}
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex flex-1 flex-col overflow-hidden">
        {/* Header */}
        <header className="border-b border-slate-200 bg-white/80 backdrop-blur-sm px-6 py-4 shadow-sm">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <span className="material-symbols-outlined text-rose-600" style={{ fontSize: "1.75rem" }}>emergency</span>
              <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Emergency Alerts</h1>
            </div>
            <div className="flex items-center gap-3">
              <button className="relative p-2.5 hover:bg-slate-100 rounded-xl transition-colors group">
                <span className="material-symbols-outlined text-slate-500 group-hover:text-slate-700" style={{ fontSize: "1.5rem" }}>notifications</span>
                <span className="absolute top-1.5 right-1.5 h-2.5 w-2.5 bg-rose-500 rounded-full animate-pulse ring-2 ring-white"></span>
              </button>
              <div className="flex items-center gap-3 pl-4 border-l border-slate-200">
                <div className="text-right">
                  <p className="text-sm font-semibold text-slate-900">{user?.email}</p>
                  <p className="text-xs text-slate-500">System Administrator</p>
                </div>
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 text-white shadow-md shadow-blue-500/30">
                  <span className="material-symbols-outlined" style={{ fontSize: "1.25rem" }}>person</span>
                </div>
              </div>
            </div>
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 overflow-auto p-6 flex flex-col gap-6">
          {/* Stats Grid */}
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
            <div className="bg-gradient-to-br from-red-600 to-rose-700 rounded-2xl p-6 shadow-xl shadow-red-500/30 text-white">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-white/70">Active Alerts</p>
                  <p className="mt-2 text-4xl font-bold">{stats.active}</p>
                </div>
                <div className="rounded-2xl bg-white/10 p-3 backdrop-blur-sm">
                  <span className="material-symbols-outlined text-white/80" style={{ fontSize: "2rem" }}>crisis_alert</span>
                </div>
              </div>
            </div>
            <div className="bg-gradient-to-br from-violet-600 to-purple-700 rounded-2xl p-6 shadow-xl shadow-violet-500/30 text-white">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-white/70">Responding</p>
                  <p className="mt-2 text-4xl font-bold">{stats.responding}</p>
                </div>
                <div className="rounded-2xl bg-white/10 p-3 backdrop-blur-sm">
                  <span className="material-symbols-outlined text-white/80" style={{ fontSize: "2rem" }}>ambulance</span>
                </div>
              </div>
            </div>
            <div className="bg-gradient-to-br from-emerald-600 to-green-700 rounded-2xl p-6 shadow-xl shadow-emerald-500/30 text-white">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-white/70">Resolved</p>
                  <p className="mt-2 text-4xl font-bold">{stats.resolved}</p>
                </div>
                <div className="rounded-2xl bg-white/10 p-3 backdrop-blur-sm">
                  <span className="material-symbols-outlined text-white/80" style={{ fontSize: "2rem" }}>check_circle</span>
                </div>
              </div>
            </div>
            <div className="bg-gradient-to-br from-orange-600 to-red-700 rounded-2xl p-6 shadow-xl shadow-orange-500/30 text-white">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-white/70">Total Casualties</p>
                  <p className="mt-2 text-4xl font-bold">{stats.totalCasualties}</p>
                </div>
                <div className="rounded-2xl bg-white/10 p-3 backdrop-blur-sm">
                  <span className="material-symbols-outlined text-white/80" style={{ fontSize: "2rem" }}>personal_injury</span>
                </div>
              </div>
            </div>
          </div>

          {/* Filters */}
          <div className="bg-white rounded-2xl p-5 shadow-lg border border-slate-200">
            <div className="flex flex-wrap items-center gap-4">
              <div className="flex items-center gap-2">
                <span className="material-symbols-outlined text-slate-500" style={{ fontSize: "1.25rem" }}>filter_list</span>
                <span className="text-sm font-bold text-slate-700">Filters:</span>
              </div>
              <div className="flex items-center gap-2">
                <label className="text-xs font-semibold text-slate-600">Status:</label>
                <select
                  value={filterStatus}
                  onChange={(e) => setFilterStatus(e.target.value)}
                  className="rounded-lg border-2 border-slate-200 bg-slate-50 px-3 py-1.5 text-sm font-medium text-slate-900 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
                >
                  <option value="All">All</option>
                  <option value="Active">Active</option>
                  <option value="Responding">Responding</option>
                  <option value="Resolved">Resolved</option>
                  <option value="Closed">Closed</option>
                </select>
              </div>
              <div className="flex items-center gap-2">
                <label className="text-xs font-semibold text-slate-600">Severity:</label>
                <select
                  value={filterSeverity}
                  onChange={(e) => setFilterSeverity(e.target.value)}
                  className="rounded-lg border-2 border-slate-200 bg-slate-50 px-3 py-1.5 text-sm font-medium text-slate-900 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500/20"
                >
                  <option value="All">All</option>
                  <option value="Critical">Critical</option>
                  <option value="High">High</option>
                  <option value="Medium">Medium</option>
                  <option value="Low">Low</option>
                </select>
              </div>
              <div className="ml-auto text-xs text-slate-500 font-medium">
                Showing {filteredAlerts.length} of {DUMMY_ALERTS.length} alerts
              </div>
            </div>
          </div>

          {/* Alerts Table */}
          <div className="bg-white rounded-2xl shadow-lg border border-slate-200 overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gradient-to-r from-slate-50 to-slate-100 border-b-2 border-slate-200">
                  <tr>
                    <th className="px-6 py-4 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">Alert Info</th>
                    <th className="px-6 py-4 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">Type</th>
                    <th className="px-6 py-4 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">Severity</th>
                    <th className="px-6 py-4 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">Status</th>
                    <th className="px-6 py-4 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">Vehicle/Personnel</th>
                    <th className="px-6 py-4 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">Location</th>
                    <th className="px-6 py-4 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">Duration</th>
                    <th className="px-6 py-4 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {filteredAlerts.map((alert) => {
                    const typeInfo = TYPE_ICONS[alert.type];
                    return (
                      <tr key={alert.id} className="hover:bg-slate-50 transition-colors">
                        <td className="px-6 py-4">
                          <div className="flex flex-col">
                            <span className="font-mono text-xs font-bold text-slate-500 mb-1">
                              {alert.alertId}
                            </span>
                            <span className="text-sm text-slate-600 font-medium">
                              {formatTimestamp(alert.timestamp)}
                            </span>
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <div className="flex items-center gap-2">
                            <span className={`material-symbols-outlined ${typeInfo.color}`} style={{ fontSize: "1.25rem" }}>
                              {typeInfo.icon}
                            </span>
                            <span className="text-sm font-semibold text-slate-900">{alert.type}</span>
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <span className={`inline-flex items-center rounded-full border px-3 py-1 text-xs font-bold uppercase ${SEVERITY_STYLES[alert.severity]}`}>
                            {alert.severity}
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <span className={`inline-flex items-center rounded-full border px-3 py-1 text-xs font-bold uppercase ${STATUS_STYLES[alert.status]}`}>
                            {alert.status}
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <div className="flex flex-col gap-1">
                            <div className="flex items-center gap-1.5">
                              <span className="material-symbols-outlined text-slate-400" style={{ fontSize: "0.875rem" }}>
                                local_shipping
                              </span>
                              <span className="text-xs font-bold text-slate-700">{alert.truck}</span>
                            </div>
                            <div className="flex items-center gap-1.5">
                              <span className="material-symbols-outlined text-slate-400" style={{ fontSize: "0.875rem" }}>
                                person
                              </span>
                              <span className="text-xs text-slate-600">{alert.personnel}</span>
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <div className="flex items-start gap-1.5 max-w-xs">
                            <span className="material-symbols-outlined text-rose-500 flex-shrink-0" style={{ fontSize: "0.875rem" }}>
                              location_on
                            </span>
                            <span className="text-xs text-slate-600 line-clamp-2">{alert.location.label}</span>
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <span className="text-xs font-mono font-bold text-slate-700">
                            {getTimeElapsed(alert.timestamp, alert.resolvedAt)}
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <button
                            onClick={() => setSelectedAlert(alert)}
                            className="flex items-center gap-1.5 rounded-lg bg-blue-50 px-3 py-2 text-xs font-bold text-blue-700 hover:bg-blue-100 transition-colors border border-blue-200"
                          >
                            <span className="material-symbols-outlined" style={{ fontSize: "1rem" }}>visibility</span>
                            View
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
        </main>
      </div>

      <style jsx>{`
        @keyframes fade-in {
          from { opacity: 0; transform: translateY(12px); }
          to   { opacity: 1; transform: translateY(0); }
        }
        .animate-fade-in {
          animation: fade-in 0.4s ease-out forwards;
          opacity: 0;
        }
      `}</style>
    </div>
  );
}
