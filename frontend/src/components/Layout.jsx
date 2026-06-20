import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">SecureMail</div>
        <p className="user-name">{user?.fullName}</p>
        <p className="user-role">{user?.role}</p>
        <nav>
          <NavLink to="/">Dashboard</NavLink>
          <NavLink to="/compose">Compose</NavLink>
          <NavLink to="/sent">Sent</NavLink>
          <NavLink to="/inbox">Inbox</NavLink>
          {user?.role === 'ADMIN' && <NavLink to="/admin/users">Admin Users</NavLink>}
          {user?.role === 'ADMIN' && <NavLink to="/admin/logs">Email Logs</NavLink>}
        </nav>
        <button className="secondary full" onClick={handleLogout}>Logout</button>
      </aside>
      <main className="content">
        <Outlet />
      </main>
    </div>
  )
}
