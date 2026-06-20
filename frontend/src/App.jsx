import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './context/AuthContext.jsx'
import Layout from './components/Layout.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import Dashboard from './pages/Dashboard.jsx'
import Compose from './pages/Compose.jsx'
import SentEmails from './pages/SentEmails.jsx'
import Inbox from './pages/Inbox.jsx'
import AdminUsers from './pages/AdminUsers.jsx'
import AdminLogs from './pages/AdminLogs.jsx'

function ProtectedRoute({ children, adminOnly = false }) {
  const { isAuthenticated, user } = useAuth()
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }
  if (adminOnly && user?.role !== 'ADMIN') {
    return <Navigate to="/" replace />
  }
  return children
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/" element={<ProtectedRoute><Layout /></ProtectedRoute>}>
        <Route index element={<Dashboard />} />
        <Route path="compose" element={<Compose />} />
        <Route path="sent" element={<SentEmails />} />
        <Route path="inbox" element={<Inbox />} />
        <Route path="admin/users" element={<ProtectedRoute adminOnly><AdminUsers /></ProtectedRoute>} />
        <Route path="admin/logs" element={<ProtectedRoute adminOnly><AdminLogs /></ProtectedRoute>} />
      </Route>
    </Routes>
  )
}
